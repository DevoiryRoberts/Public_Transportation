package com.example.public_transportation_project.DTOs;

import com.example.public_transportation_project.Models.BusStatus; 
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "אובייקט ליצירת או עדכון אוטובוס")
public class BusRequest {

    @NotBlank(message = "לוחית רישוי היא שדה חובה")
    @Pattern(regexp = "^[0-9]{7,8}$", message = "לוחית רישוי חייבת להכיל 7 או 8 ספרות")
    @Schema(description = "מספר לוחית רישוי (ספרות בלבד)", example = "12345678")
    private String licensePlate;

    @Min(value = 1, message = "מספר המקומות חייב להיות לפחות 1")
    @Max(value = 100, message = "מספר מקומות מקסימלי הוא 100")
    @Schema(description = "קיבולת נוסעים מקסימלית", example = "55")
    private int capacity;

    @NotNull(message = "חובה לבחור סטטוס")
    @Schema(
    description = "סטטוס האוטובוס", 
    type = "string", 
    allowableValues = {"AVAILABLE", "IN_SERVICE", "UNDER_MAINTENANCE", "OUT_OF_ORDER"},
    example = "AVAILABLE"
    )
    private BusStatus status;

    @PastOrPresent(message = "תאריך טסט לא יכול להיות בעתיד")
    private LocalDate lastTestDate;

    
}