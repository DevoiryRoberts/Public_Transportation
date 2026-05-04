package com.example.public_transportation_project.Mappers;

import com.example.public_transportation_project.Models.Travel;
import com.example.public_transportation_project.DTOs.TravelResponseDTO;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.List;

@Component
public class TravelMapper {

    public static TravelResponseDTO toResponseDTO(Travel travel) {
        if (travel == null) return null;

        List<String> stationNames = travel.getBusLine().getStations().stream()
                .map(sil -> sil.getStation().getName())
                .toList();

        long duration = 0;
        if (travel.getDepartureTime() != null && travel.getArrivalTime() != null) {
            duration = Duration.between(travel.getDepartureTime(), travel.getArrivalTime()).toMinutes();
        }

        return new TravelResponseDTO(
                travel.getId(),
                travel.getDepartureTime(),
                travel.getArrivalTime(),
                duration,
                travel.getBusLine().getLineNumber(),
                travel.getBusLine().getOrigin(),
                travel.getBusLine().getDestination(),
                stationNames,
                travel.getDriver().getName(),
                travel.getDriver().getRating(),
                travel.getDriver().getRole(),
                travel.getBus().getLicensePlate(),
                travel.getBus().getStatus().toString(),
                travel.getStatus().toString()
        );
    }
}