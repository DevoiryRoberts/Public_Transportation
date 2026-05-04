package com.example.public_transportation_project.Services;

import com.example.public_transportation_project.Models.*;
import com.example.public_transportation_project.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.public_transportation_project.DTOs.BusLineRequest;
import com.example.public_transportation_project.DTOs.BusLineResponse;
import com.example.public_transportation_project.DTOs.BusLineUpdate;
import com.example.public_transportation_project.Mappers.BusLineMapper;
import java.time.LocalDateTime;
import java.util.Optional;
import com.example.public_transportation_project.Models.ArchiveMode;
import java.util.List;

@Service
public class BusLineService {

    @Autowired
    private BusLineRepository busLineRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private StationInLineRepository stationInLineRepository;

    @Autowired
    private TravelRepository travelRepository;

    @Transactional
    public BusLineResponse createBusLine(BusLineRequest dto) {

        Optional<BusLine> existingLine = busLineRepository.findAllIncludeDeleted().stream()
                .filter(l -> l.getLineNumber() == dto.getLineNumber()
                && l.getOrigin().equalsIgnoreCase(dto.getOrigin())
                && l.getDestination().equalsIgnoreCase(dto.getDestination()))
                .findFirst();

        if (existingLine.isPresent()) {
            BusLine line = existingLine.get();

            if (line.isActive()) {
                throw new RuntimeException("שגיאה: קו " + dto.getLineNumber() + " כבר קיים ופעיל.");
            }

            return restoreBusLine(line.getId());
        }

        BusLine busLine = new BusLine();
        busLine.setLineNumber(dto.getLineNumber());
        busLine.setOrigin(dto.getOrigin());
        busLine.setDestination(dto.getDestination());
        busLine.setActive(true);

        BusLine savedLine = busLineRepository.save(busLine);
        return BusLineMapper.toResponse(savedLine, stationInLineRepository);    }

    @Transactional
    public List<BusLine> getAllLines() {
        return busLineRepository.findAll();
    }

    @Transactional
    public void addStationToLine(Long lineId, Long stationId, int order) {
        BusLine line = busLineRepository.findByIdIncludeDeleted(lineId)
                .orElseThrow(() -> new RuntimeException("Line not found"));

        if (!line.isActive()) {
            throw new RuntimeException("שגיאה: לא ניתן להוסיף תחנות לקו שנמצא בארכיון. יש לשחזר את הקו תחילה.");
        }

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        StationInLineId silId = new StationInLineId(lineId, stationId);
        if (stationInLineRepository.existsById(silId)) {
            throw new RuntimeException("התחנה כבר קיימת במסלול של קו זה.");
        }

        List<StationInLine> currentStations = stationInLineRepository.findByBusLineIdAndIsActiveTrueOrderByStationOrderAsc(lineId);
        for (StationInLine sil : currentStations) {
            if (sil.getStationOrder() >= order) {
                sil.setStationOrder(sil.getStationOrder() + 1);
                stationInLineRepository.save(sil);
            }
        }

        StationInLine newLink = new StationInLine();
        newLink.setId(silId);
        newLink.setBusLine(line);
        newLink.setStation(station);
        newLink.setStationOrder(order);
        newLink.setActive(true);

        stationInLineRepository.save(newLink);
    }

    public List<BusLineResponse> getLinesByArchiveMode(ArchiveMode mode) {
        List<BusLine> lines = switch (mode) {
            case ACTIVE_ONLY ->
                busLineRepository.findByIsActiveTrue();
            case DELETED_ONLY ->
                busLineRepository.findByIsActiveFalse();
            case ALL ->
                busLineRepository.findAllIncludeDeleted();
        };
        return lines.stream().map(line -> BusLineMapper.toResponse(line, stationInLineRepository)).toList();
    }

    @Transactional
    public void removeStationFromLine(Long lineId, Long stationId) {
        StationInLineId silId = new StationInLineId(lineId, stationId);
        StationInLine toRemove = stationInLineRepository.findById(silId)
                .orElseThrow(() -> new RuntimeException("This station is not part of this line"));

        int removedOrder = toRemove.getStationOrder();
        stationInLineRepository.delete(toRemove);

        List<StationInLine> stationsInLine = stationInLineRepository.findByBusLineIdAndIsActiveTrueOrderByStationOrderAsc(lineId);
        for (StationInLine sil : stationsInLine) {
            if (sil.getStationOrder() > removedOrder) {
                sil.setStationOrder(sil.getStationOrder() - 1);
                stationInLineRepository.save(sil);
            }
        }
    }

    @Transactional
    public BusLine updateBusLine(Long id, BusLineUpdate dto) {
        BusLine line = busLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("הקו לא נמצא"));

        if (!line.isActive()) {
            throw new RuntimeException("שגיאה: לא ניתן לעדכן פרטים של קו שנמצא בארכיון.");
        }

        if (dto.getLineNumber() != null) {
            line.setLineNumber(dto.getLineNumber());
        }
        if (dto.getOrigin() != null) {
            line.setOrigin(dto.getOrigin());
        }
        if (dto.getDestination() != null) {
            line.setDestination(dto.getDestination());
        }

        return busLineRepository.save(line);
    }

    @Transactional
    public void deleteBusLine(Long id) {
        if (travelRepository.existsByBusLineIdAndDepartureTimeAfter(id, LocalDateTime.now())) {
            throw new RuntimeException("לא ניתן למחוק: קיימות נסיעות עתידיות לקו זה!");
        }

        BusLine line = busLineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("הקו לא נמצא"));

        line.setActive(false);

        List<StationInLine> connections = stationInLineRepository.findByBusLineIdAndIsActiveTrueOrderByStationOrderAsc(id);
        for (StationInLine sil : connections) {
            sil.setActive(false);
        }

        busLineRepository.save(line);
    }

    @Transactional
    public void hardDeleteBusLine(Long id) {
        BusLine line = busLineRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new RuntimeException("הקו לא נמצא, לא ניתן למחוק."));

        travelRepository.deleteByBusLineId(id);

        stationInLineRepository.deleteByBusLineId(id);

        busLineRepository.hardDeleteById(id);
    }

    @Transactional
    public BusLineResponse restoreBusLine(Long id) {
        BusLine line = busLineRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new RuntimeException("הקו לא נמצא במערכת"));

        if (line.isActive()) {
            throw new RuntimeException("שגיאה: קו מספר " + line.getLineNumber() + " כבר פעיל במערכת ואינו נמצא בארכיון.");
        }

        line.setActive(true);

        List<StationInLine> connections = stationInLineRepository.findByBusLineId(id);
        for (StationInLine sil : connections) {
            sil.setActive(true);
        }

        busLineRepository.save(line);

        return BusLineMapper.toResponse(line, stationInLineRepository);
    }

    private void validateBusLineUniqueness(int lineNumber, String origin, String destination) {
        Optional<BusLine> existingLine = busLineRepository.findAllIncludeDeleted().stream()
                .filter(l -> l.getLineNumber() == lineNumber
                && l.getOrigin().equalsIgnoreCase(origin)
                && l.getDestination().equalsIgnoreCase(destination))
                .findFirst();

        if (existingLine.isPresent()) {
            if (existingLine.get().isActive()) {
                throw new RuntimeException("שגיאה: קו " + lineNumber + " כבר קיים ופעיל.");
            } else {
                throw new RuntimeException("שגיאה: קו " + lineNumber + " קיים בארכיון. אנא השתמש באופציית השחזור (Restore) במקום ליצור חדש.");
            }
        }
    }

}
