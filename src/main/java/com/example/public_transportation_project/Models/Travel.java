package com.example.public_transportation_project.Models;

import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE travel SET is_active = false WHERE id=?")
@SQLRestriction("is_active = true")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Travel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bus_line_id")
    private BusLine busLine;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private Bus bus;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Driver driver;

    @Column(name = "departure_time")
    private LocalDateTime departureTime;

    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    private TravelStatus status = TravelStatus.PLANNED;

    @Column(name = "is_active")
    private boolean isActive = true;
}
