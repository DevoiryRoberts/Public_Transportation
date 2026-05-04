package com.example.public_transportation_project.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DriverUpdate {

    private String name;

    @Pattern(regexp = "^\\d{10}$", message = "טלפון חייב להכיל 10 ספרות")
    private String phone;

    @Email(message = "אימייל לא תקין")
    private String email;

    private String licenseNumber;
}
