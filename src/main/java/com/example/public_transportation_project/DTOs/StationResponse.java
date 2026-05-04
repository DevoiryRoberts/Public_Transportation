package com.example.public_transportation_project.DTOs;

import lombok.Data;

@Data
public class StationResponse {

    private Long id;
    private String name;
    private String address;
    private boolean isActive;
}
