package com.example.public_transportation_project.Mappers;

import com.example.public_transportation_project.Models.Station;
import com.example.public_transportation_project.DTOs.StationRequest;
import com.example.public_transportation_project.DTOs.StationResponse; // אם קיים
import org.springframework.stereotype.Component;

@Component
public class StationMapper {

    public static void updateEntityFromDto(StationRequest dto, Station station) {
        if (dto == null || station == null) return;
        station.setName(dto.getName());
        station.setAddress(dto.getAddress());
    }

    public static Station toEntity(StationRequest dto) {
        if (dto == null) return null;
        Station station = new Station();
        updateEntityFromDto(dto, station);
        station.setActive(true);
        return station;
    }
}