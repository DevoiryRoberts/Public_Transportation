package com.example.public_transportation_project.Mappers;

import com.example.public_transportation_project.Models.Driver;
import com.example.public_transportation_project.DTOs.DriverRequest;
import com.example.public_transportation_project.DTOs.DriverUpdate;
import com.example.public_transportation_project.DTOs.DriverResponse; // אם יצרת כזה, אם לא השתמשי ב-Driver
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public static void updateEntityFromRequest(DriverRequest dto, Driver driver) {
        if (dto == null || driver == null) return;
        driver.setName(dto.getName());
        driver.setPhone(dto.getPhone());
        driver.setEmail(dto.getEmail());
        driver.setLicenseNumber(dto.getLicenseNumber());
        driver.setRating(dto.getRating());
    }

    public static void updateEntityFromPatch(DriverUpdate dto, Driver driver) {
        if (dto == null || driver == null) return;
        if (dto.getName() != null) driver.setName(dto.getName());
        if (dto.getEmail() != null) driver.setEmail(dto.getEmail());
        if (dto.getPhone() != null) driver.setPhone(dto.getPhone());
        if (dto.getLicenseNumber() != null) driver.setLicenseNumber(dto.getLicenseNumber());
    }
}