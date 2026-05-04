package com.example.public_transportation_project.Mappers;

import com.example.public_transportation_project.Models.BusLine;
import com.example.public_transportation_project.DTOs.BusLineResponse;
import com.example.public_transportation_project.Repositories.StationInLineRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BusLineMapper {

    public static BusLineResponse toResponse(BusLine line, StationInLineRepository stationInLineRepository) {
        if (line == null) return null;

        List<String> stationNames = stationInLineRepository
                .findByBusLineId(line.getId())
                .stream()
                .map(sil -> sil.getStation().getName())
                .toList();

        return new BusLineResponse(
                line.getId(),
                line.getLineNumber(),
                line.getOrigin(),
                line.getDestination(),
                stationNames,
                line.isActive()
        );
    }
}