package com.example.public_transportation_project.Services;

import com.example.public_transportation_project.Models.*;
import com.example.public_transportation_project.Repositories.*;
import com.example.public_transportation_project.DTOs.TravelRequest;
import com.example.public_transportation_project.DTOs.TravelResponseDTO;
import com.example.public_transportation_project.Mappers.TravelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TravelService {

    @Autowired
    private TravelRepository travelRepository;
    @Autowired
    private StationInLineRepository stationInLineRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private BusLineRepository busLineRepository;
    @Autowired
    private DriverRepository driverRepository;
    @PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    @Transactional
    public Travel scheduleTravel(TravelRequest dto) {
        Bus bus = busRepository.findById(dto.getBusId())
                .orElseThrow(() -> new RuntimeException("אוטובוס לא נמצא"));
        BusLine line = busLineRepository.findById(dto.getBusLineId())
                .orElseThrow(() -> new RuntimeException("קו לא נמצא"));
        Driver driver = driverRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא"));

        if (bus.getStatus() != BusStatus.AVAILABLE) {
            throw new RuntimeException("האוטובוס אינו פנוי. סטטוס: " + bus.getStatus());
        }

        if (driver.getRating() < 2 && driver.getRating() != 0) {
            throw new RuntimeException("לא ניתן לשבץ את הנהג " + driver.getName() + " עקב דירוג נמוך מדי. נדרשת הכשרה מחדש.");
        }

        List<StationInLine> stations = stationInLineRepository.findByBusLineId(line.getId());
        if (stations.isEmpty()) {
            throw new RuntimeException("שגיאה: לא ניתן לשבץ נסיעה לקו ללא תחנות.");
        }

        int estimatedDuration = (stations.size() * 1) + 10;
        LocalDateTime arrivalTime = dto.getDepartureTime().plusMinutes(estimatedDuration);

        LocalDateTime checkStart = dto.getDepartureTime().minusMinutes(15);
        LocalDateTime checkEnd = arrivalTime.plusMinutes(15);

        if (travelRepository.isDriverBusy(dto.getDriverId(), checkStart, checkEnd)) {
            throw new RuntimeException("הנהג כבר משובץ לנסיעה חופפת או נמצא בהפסקה");
        }
        if (travelRepository.isBusBusy(dto.getBusId(), checkStart, checkEnd)) {
            throw new RuntimeException("האוטובוס כבר משובץ לנסיעה חופפת בזמן זה");
        }

        Travel travel = new Travel();
        travel.setBus(bus);
        travel.setBusLine(line);
        travel.setDriver(driver);
        travel.setDepartureTime(dto.getDepartureTime());
        travel.setArrivalTime(arrivalTime);
        travel.setActive(true);

        return travelRepository.save(travel);
    }

    public Travel saveTravel(Travel travel) {
        return travelRepository.save(travel);
    }

    public List<String> getBusLocationsOnLine(Long lineId) {
        LocalDateTime now = LocalDateTime.now();

        List<Travel> activeTravels = travelRepository.findByBusLineIdAndIsActiveTrue(lineId).stream()
                .filter(t -> t.getDepartureTime() != null && t.getArrivalTime() != null)
                .filter(t -> t.getDepartureTime().isBefore(now) && t.getArrivalTime().isAfter(now))
                .collect(Collectors.toList());

        if (activeTravels.isEmpty()) {
            return List.of("אין אוטובוסים פעילים בקו זה כרגע.");
        }

        return activeTravels.stream()
                .map(t -> {
                    String nextStation = calculateNextStation(t, now);

                    return String.format("אוטובוס [%s] בדרך מ%s ל%s. תחנה קרובה: %s. הגעה משוערת ליעד: %s",
                            t.getBus().getLicensePlate(),
                            t.getBusLine().getOrigin(),
                            t.getBusLine().getDestination(),
                            nextStation,
                            t.getArrivalTime().toLocalTime().toString());
                })
                .collect(Collectors.toList());
    }

    private String calculateNextStation(Travel travel, LocalDateTime now) {
        List<StationInLine> stations = travel.getBusLine().getStations();
        if (stations == null || stations.isEmpty()) {
            return "מידע על תחנות אינו זמין";
        }

        long minutesSinceDeparture = java.time.Duration.between(travel.getDepartureTime(), now).toMinutes();

        int stationIndex = (int) (minutesSinceDeparture / 5);

        if (stationIndex >= stations.size() - 1) {
            return "מתקרב ליעד סופי: " + travel.getBusLine().getDestination();
        }

        return stations.get(stationIndex + 1).getStation().getName();
    }

    public Travel getLastTravelOfLine(Long lineId) {
        Travel lastTravel = travelRepository.findTopByBusLineIdAndIsActiveTrueOrderByDepartureTimeDesc(lineId)
                .orElseThrow(() -> new RuntimeException("לא נמצאה נסיעה אחרונה לקו זה"));
        return lastTravel;
    }

    @Transactional
    public TravelResponseDTO updateTravel(Long id, TravelRequest dto) {

        Travel travel = travelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("נסיעה לא נמצאה"));

        LocalDateTime startTime = (dto.getDepartureTime() != null) ? dto.getDepartureTime() : travel.getDepartureTime();

        int duration = travel.getBusLine().getStations().size() * 1;
        LocalDateTime endTime = startTime.plusMinutes(duration);

        if (dto.getDriverId() != null) {
            if (travelRepository.isDriverBusy(dto.getDriverId(), startTime, endTime) && !travel.getDriver().getId().equals(dto.getDriverId())) {
                throw new RuntimeException("הנהג החדש תפוס בזמן המבוקש");
            }
            Driver driver = driverRepository.findById(dto.getDriverId()).orElseThrow();
            travel.setDriver(driver);
        }

        if (dto.getBusId() != null) {
            if (travelRepository.isBusBusy(dto.getBusId(), startTime, endTime) && !travel.getBus().getId().equals(dto.getBusId())) {
                throw new RuntimeException("האוטובוס החדש תפוס בזמן המבוקש");
            }
            Bus bus = busRepository.findById(dto.getBusId()).orElseThrow();
            travel.setBus(bus);
        }

        if (dto.getDepartureTime() != null) {
            travel.setDepartureTime(startTime);
            travel.setArrivalTime(endTime);
        }

        return TravelMapper.toResponseDTO(travelRepository.save(travel));
    }

    public void deleteToArchive(Long id) {
        if (!travelRepository.existsById(id)) {
            throw new RuntimeException("נסיעה לא נמצאה");
        }
        travelRepository.deleteById(id);
    }

    @Transactional
    public void hardDelete(Long id) {
        travelRepository.deleteById(id);

        entityManager.createNativeQuery("DELETE FROM travel WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<TravelResponseDTO> getFilteredTravelsForAdmin(ArchiveMode archiveMode, TravelStatus status) {
        List<Travel> travels;

        switch (archiveMode) {
            case DELETED_ONLY:
                travels = travelRepository.findDeletedOnly();
                break;
            case ALL:
                travels = travelRepository.findAllIncludingArchive();
                break;
            default:
                travels = travelRepository.findByIsActiveTrue();
                break;
        }

        if (status != null) {
            travels = travels.stream()
                    .filter(t -> t.getStatus() == status)
                    .collect(Collectors.toList());
        }

        LocalDateTime now = LocalDateTime.now();
        return travels.stream()
                .map(t -> {
                    if (t.isActive() && t.getDepartureTime() != null && t.getArrivalTime() != null
                            && t.getStatus() != TravelStatus.CANCELLED) {

                        if (now.isBefore(t.getDepartureTime())) {
                            t.setStatus(TravelStatus.PLANNED);
                        } else if (now.isAfter(t.getArrivalTime())) {
                            t.setStatus(TravelStatus.COMPLETED);
                        } else {
                            t.setStatus(TravelStatus.ON_ROAD);
                        }
                    }
                    return TravelMapper.toResponseDTO(t);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void restoreFromArchive(Long id) {

        Travel travel = travelRepository.findAllIncludingArchive().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("נסיעה לא נמצאה בארכיון"));

        if (!driverRepository.existsById(travel.getDriver().getId())) {
            throw new RuntimeException("לא ניתן לשחזר: הנהג המשויך לנסיעה זו כבר לא פעיל במערכת");
        }

        if (!busRepository.existsById(travel.getBus().getId())) {
            throw new RuntimeException("לא ניתן לשחזר: האוטובוס המשויך לנסיעה זו כבר לא פעיל במערכת");
        }

        travel.setActive(true);
        travelRepository.save(travel);
    }

    public List<TravelResponseDTO> searchTravels(String origin, String destination) {
        LocalDateTime now = LocalDateTime.now();
        return travelRepository.findByIsActiveTrue().stream()
                .filter(t -> t.getBusLine().getOrigin().equalsIgnoreCase(origin)
                && t.getBusLine().getDestination().equalsIgnoreCase(destination))
                .filter(t -> t.getDepartureTime().isAfter(now)) // רק נסיעות עתידיות
                .map(TravelMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public String getMostActiveLine() {
        return travelRepository.findByIsActiveTrue().stream()
                .collect(Collectors.groupingBy(t -> t.getBusLine().getLineNumber(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> "הקו הפעיל ביותר הוא " + entry.getKey() + " עם " + entry.getValue() + " נסיעות מתוכננות.")
                .orElse("אין מספיק נתונים לחישוב.");
    }

    public List<String> getDriverTravelStats() {

        Map<String, Long> statsMap = travelRepository.findByIsActiveTrue().stream()
                .filter(t -> t.getDriver() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getDriver().getName(),
                        Collectors.counting()
                ));

        return statsMap.entrySet().stream()
                .map(entry -> "הנהג הוא: " + entry.getKey() + ", עם כמות נסיעות: " + entry.getValue())
                .collect(Collectors.toList());
    }

    public String getArrivalTimeForStation(Long lineId, int stationOrder) {
        LocalDateTime now = LocalDateTime.now();

        return travelRepository.findByBusLineIdAndIsActiveTrue(lineId).stream()
                .filter(t -> t.getDepartureTime().isAfter(now)
                || (t.getDepartureTime().isBefore(now) && t.getArrivalTime().isAfter(now)))
                .findFirst()
                .map(t -> {
                    LocalDateTime arrivalTime = t.getDepartureTime().plusMinutes(stationOrder);

                    String status = now.isAfter(t.getDepartureTime()) ? "כבר יצא" : "עתיד לצאת";

                    return String.format("הקו מגיע לתחנה %d בשעה %s. (האוטובוס %s ב-%s)",
                            stationOrder,
                            arrivalTime.toLocalTime().toString(),
                            status,
                            t.getDepartureTime().toLocalTime().toString());
                })
                .orElse("לא נמצאו נסיעות פעילות או עתידיות לקו זה.");
    }

    public List<String> getAllStationsOnLine(Long lineId) {
        return stationInLineRepository.findByBusLineIdAndIsActiveTrueOrderByStationOrderAsc(lineId)
                .stream()
                .map(sil -> sil.getStationOrder() + ". " + sil.getStation().getName())
                .collect(Collectors.toList());
    }

}
