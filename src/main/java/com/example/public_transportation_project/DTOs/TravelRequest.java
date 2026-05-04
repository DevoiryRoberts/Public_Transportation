package com.example.public_transportation_project.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

@Data
public class TravelRequest {

    @NotNull(message = "חובה לבחור קו נסיעה")
    private Long busLineId;

    @NotNull(message = "חובה לבחור אוטובוס")
    private Long busId;

    @NotNull(message = "חובה לבחור נהג")
    private Long driverId;

    @NotNull(message = "חובה לציין זמן יציאה")
    @FutureOrPresent(message = "זמן היציאה לא יכול להיות בעבר")
    private LocalDateTime departureTime;
}
