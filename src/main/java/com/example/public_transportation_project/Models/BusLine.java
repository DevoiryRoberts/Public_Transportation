package com.example.public_transportation_project.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@SQLDelete(sql = "UPDATE bus_line SET is_active = false WHERE id=?")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_number")
    @Min(1)
    private int lineNumber;

    private String origin;
    private String destination;

    @JsonManagedReference
    @OneToMany(mappedBy = "busLine", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stationOrder ASC")
    @JsonIgnore
    private List<StationInLine> stations;

    @Column(name = "is_active")
    private boolean isActive = true;
}
