package com.example.public_transportation_project.Services;

import com.example.public_transportation_project.Models.Bus;
import com.example.public_transportation_project.Models.BusStatus;
import com.example.public_transportation_project.DTOs.BusRequest;
import com.example.public_transportation_project.DTOs.BusResponse;
import com.example.public_transportation_project.Mappers.BusMapper;
import com.example.public_transportation_project.Repositories.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.public_transportation_project.Models.ArchiveMode;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class BusService {

    @Autowired
    private BusRepository busRepository;

    public List<Bus> getAllBuses(BusStatus status) {
        if (status != null) {
            return busRepository.findByStatusAndIsActiveTrue(status);
        }
        return busRepository.findAll();
    }

    @Transactional
    public BusResponse saveBus(BusRequest dto) { 
        if (busRepository.existsByLicensePlate(dto.getLicensePlate())) {
            throw new RuntimeException("שגיאה: אוטובוס עם לוחית רישוי זו כבר רשום במערכת");
        }

        Bus bus = new Bus();
        BusMapper.updateEntityFromDto(dto, bus);
        return BusMapper.toResponse(busRepository.save(bus));
    }

    @Transactional
    public Bus updateBus(Long id, BusRequest dto) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("אוטובוס מספר " + id + " לא נמצא"));

        busRepository.findByLicensePlateIncludeDeleted(dto.getLicensePlate()).ifPresent(existingBus -> {
            if (!existingBus.getId().equals(id)) {
                throw new RuntimeException("שגיאה: לוחית הרישוי " + dto.getLicensePlate() + " כבר קיימת במערכת עבור אוטובוס אחר");
            }
        });

        BusMapper.updateEntityFromDto(dto, bus);

        return busRepository.save(bus);
    }

    @Transactional
    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new RuntimeException("אוטובוס לא נמצא או שכבר נמחק");
        }
        busRepository.deleteById(id);
    }

    @Transactional
    public void restoreBus(Long id) {
        Bus bus = busRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new RuntimeException("אוטובוס לא נמצא בארכיון"));
        bus.setActive(true);
        busRepository.save(bus);
    }

    @Transactional
    public void forceHardDelete(Long id) {

        busRepository.hardDeleteById(id);

    }

    public List<Bus> getBusesNeedingTest() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        return busRepository.findAll().stream()
                .filter(bus -> bus.getLastTestDate() == null || bus.getLastTestDate().isBefore(oneYearAgo))
                .toList();
    }

    public List<Bus> getAvailableBuses() {
        return busRepository.findByStatusAndIsActiveTrue(BusStatus.AVAILABLE);
    }

    public List<Bus> getBusesAdvanced(BusStatus status, ArchiveMode mode) {
        String statusStr = (status != null) ? status.name() : null;
        return busRepository.findBusesAdvanced(statusStr, mode.name());
    }

}
