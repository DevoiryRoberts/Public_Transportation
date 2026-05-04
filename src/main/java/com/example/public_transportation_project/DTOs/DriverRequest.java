package com.example.public_transportation_project.DTOs;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverRequest {

    @NotBlank(message = "שם הנהג לא יכול להיות ריק")
    @Size(min = 2, max = 50, message = "שם חייב להיות בין 2 ל-50 תווים")
    private String name;

    @NotBlank(message = "חובה להזין מספר טלפון")
    @Pattern(regexp = "^\\d{10}$", message = "טלפון חייב להכיל בדיוק 10 ספרות")
    private String phone;

    @NotBlank(message = "חובה להזין אימייל")
    @Email(message = "פורמט האימייל אינו תקין")
    private String email;

    @NotBlank(message = "מספר רישיון הוא שדה חובה")
    @Pattern(regexp = "^[0-9]{7,9}$", message = "מספר רישיון חייב להכיל בין 7 ל-9 ספרות")
    private String licenseNumber;

    @Min(value = 1, message = "דירוג מינימלי הוא 1")
    @Max(value = 5, message = "דירוג מקסימלי הוא 5")
    private int rating;
}
