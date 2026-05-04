package com.example.public_transportation_project.DTOs;

import lombok.Data;

@Data
public class DriverResponse { 

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private int rating;
    private String role;
    private boolean isActive;
}
