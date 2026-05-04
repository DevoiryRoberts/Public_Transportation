package com.example.public_transportation_project.Mappers;

import com.example.public_transportation_project.Models.Bus;
import com.example.public_transportation_project.DTOs.BusResponse; // ודאי שיש לך DTO כזה, אם לא השתמשי ב-Bus
import org.springframework.stereotype.Component;

@Component
public class BusMapper {

    public static BusResponse toResponse(Bus bus) {
        if (bus == null) return null;
        
        BusResponse dto = new BusResponse();
        dto.setId(bus.getId());
        dto.setLicensePlate(bus.getLicensePlate());
        dto.setCapacity(bus.getCapacity());
        dto.setStatus(bus.getStatus());
        dto.setLastTestDate(bus.getLastTestDate());
        dto.setActive(bus.isActive());
        
        return dto;
    }

    public static void updateEntityFromDto(com.example.public_transportation_project.DTOs.BusRequest dto, Bus bus) {
        if (dto == null || bus == null) return;
        
        bus.setLicensePlate(dto.getLicensePlate());
        bus.setCapacity(dto.getCapacity());
        bus.setStatus(dto.getStatus());
        bus.setLastTestDate(dto.getLastTestDate());
    }
}