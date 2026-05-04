package com.example.public_transportation_project.Models; 

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "מצב תצוגת ארכיון")
public enum ArchiveMode {

    ACTIVE_ONLY,
    DELETED_ONLY,
    ALL
    
}
