package com.example.public_transportation_project.DTOs;

import com.example.public_transportation_project.Models.BusStatus;
import lombok.Data;

@Data
public class BusResponse {

    private Long id;
    private String licensePlate;
    private int capacity;
    private BusStatus status;
    private java.time.LocalDate lastTestDate;
    private boolean isActive;
}
