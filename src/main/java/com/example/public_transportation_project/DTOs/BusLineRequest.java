package com.example.public_transportation_project.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BusLineRequest {

    @Min(value = 1, message = "מספר קו חייב להיות חיובי")
    private int lineNumber;

    @NotBlank(message = "מוצא לא יכול להיות ריק")
    @Size(min = 2, message = "שם עיר מוצא חייב להכיל לפחות 2 אותיות")
    private String origin;

    @NotBlank(message = "יעד לא יכול להיות ריק")
    @Size(min = 2, message = "שם עיר יעד חייב להכיל לפחות 2 אותיות")
    private String destination;
}