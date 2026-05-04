package com.example.public_transportation_project.Models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "מצב האוטובוס")
public enum BusStatus {

    AVAILABLE,
    IN_TRANSIT,
    MAINTENANCE,
    OUT_OF_SERVICE

}
