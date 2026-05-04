package com.example.public_transportation_project.Models;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationInLineId implements Serializable {

    private Long busLineId;
    private Long stationId;
}
