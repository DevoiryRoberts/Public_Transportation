package com.example.public_transportation_project.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusLineResponse {
    
    private Long id;
    private int lineNumber;
    private String origin;
    private String destination;
    private List<String> stationNames;
    private boolean active; 
}