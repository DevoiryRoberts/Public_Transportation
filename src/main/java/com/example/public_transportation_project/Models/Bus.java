package com.example.public_transportation_project.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.EntityListeners;
import java.time.LocalDate;

@Entity
@SQLDelete(sql = "UPDATE bus SET is_active = false WHERE id=?")
@SQLRestriction("is_active = true")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "לוחית רישוי היא שדה חובה")
    @Column(unique = true, name = "license_plate")
    private String licensePlate;

    @Min(value = 1, message = "מספר המקומות חייב להיות לפחות 1")
    @Max(value = 100, message = "מספר מקומות מקסימלי הוא 100")
    private int capacity;

    @Enumerated(EnumType.STRING)
    private BusStatus status = BusStatus.AVAILABLE;

    private LocalDate lastTestDate;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

}
