package com.example.public_transportation_project.DTOs;

import java.time.LocalDateTime;
import lombok.Data;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelResponseDTO {

    private Long id;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private long estimatedDurationMinutes;

    private int lineNumber;
    private String origin;
    private String destination;
    private List<String> stationNames;

    private String driverName;
    private int driverRating;
    private String driverRole;

    private String busLicensePlate;
    private String busStatus;

    private String travelStatus;
}
