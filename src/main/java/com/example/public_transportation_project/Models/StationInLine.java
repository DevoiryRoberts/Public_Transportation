package com.example.public_transportation_project.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@SQLDelete(sql = "UPDATE station_in_line SET is_active = false WHERE bus_line_id = ? AND station_id = ?")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationInLine {

    @EmbeddedId
    private StationInLineId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("busLineId")
    @JsonIgnore
    private BusLine busLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("stationId")
    private Station station;

    @Column(name = "station_order")
    private int stationOrder;

    @Column(name = "is_active")
    private boolean isActive = true;
}
