package com.example.public_transportation_project.DTOs;

import lombok.Data;

@Data
public class BusLineUpdate {
    
    private Integer lineNumber; 
    private String origin;
    private String destination;
}