package com.example.public_transportation_project.Services;

import com.example.public_transportation_project.Models.Station;
import com.example.public_transportation_project.DTOs.StationRequest;
import com.example.public_transportation_project.Mappers.StationMapper;
import com.example.public_transportation_project.Repositories.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.public_transportation_project.Models.ArchiveMode;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StationService {

    @Autowired
    private StationRepository stationRepository;

@Autowired
private StationMapper stationMapper;

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public List<Station> searchStations(String name) {
        return stationRepository.findByNameContainingAndIsActiveTrue(name);
    }

    @Transactional
    public Station createStation(StationRequest dto) {
        return stationRepository.findByNameAndAddressIncludeDeleted(dto.getName(), dto.getAddress())
                .map(existingStation -> {
                    if (!existingStation.isActive()) {
                        existingStation.setActive(true);
                        return stationRepository.save(existingStation);
                    }
                    throw new RuntimeException("התחנה כבר קיימת ופעילה");
                })
                .orElseGet(() -> {
                    Station newStation = StationMapper.toEntity(dto);
                    return stationRepository.save(newStation);
                });
    }

    @Transactional
    public Station updateStation(Long id, StationRequest dto) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("תחנה לא נמצאה"));

        StationMapper.updateEntityFromDto(dto, station);
        return stationRepository.save(station);
    }

    @Transactional
    public void deleteStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("תחנה לא נמצאה"));

        if (station.getLinesInStation() != null && !station.getLinesInStation().isEmpty()) {
            throw new RuntimeException("לא ניתן למחוק תחנה המשויכת לקווים פעילים!");
        }
        stationRepository.deleteById(id);
    }

    @Transactional
    public void restoreStation(Long id) {
        Station station = stationRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new RuntimeException("תחנה עם מזהה " + id + " לא קיימת בכלל במערכת"));

        if (station.isActive()) {
            throw new RuntimeException("התחנה עם מזהה " + id + " כבר פעילה ואינה נמצאת בארכיון");
        }

        station.setActive(true);
        stationRepository.save(station);
    }

    @Transactional
    public void forceHardDelete(Long id) {
        Station station = stationRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new RuntimeException("תחנה לא נמצאה"));

        if (station.getLinesInStation() != null && !station.getLinesInStation().isEmpty()) {
            throw new RuntimeException("חסימת אבטחה: לא ניתן למחוק פיזית תחנה שיש לה היסטוריה של קווים.");
        }
        stationRepository.hardDeleteById(id);
    }

    public List<Station> getStationsAdvanced(ArchiveMode mode) {
        return stationRepository.findStationsAdvanced(mode.name());
    }

}
