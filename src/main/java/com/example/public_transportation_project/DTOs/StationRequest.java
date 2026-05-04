package com.example.public_transportation_project.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "אובייקט ליצירת או עדכון תחנה")
public class StationRequest {

    @NotBlank(message = "שם תחנה הוא שדה חובה")
    @Schema(description = "שם התחנה", example = "מסוף עזרא")
    private String name;

    @NotBlank(message = "כתובת תחנה היא שדה חובה")
    @Schema(description = "כתובת פיזית של התחנה", example = "עזרא 45, בני ברק")
    private String address;
}
