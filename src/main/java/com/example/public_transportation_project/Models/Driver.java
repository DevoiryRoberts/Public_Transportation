package com.example.public_transportation_project.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import com.fasterxml.jackson.annotation.JsonView;
import com.example.public_transportation_project.Config.Views;
import java.time.LocalDateTime;

@Entity
@SQLDelete(sql = "UPDATE driver SET is_active = false WHERE id=?")
@SQLRestriction("is_active = true")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    private Long id;

    @NotBlank(message = "שם חובה")
    @JsonView(Views.Public.class)
    private String name;

    @Pattern(regexp = "^\\d{10}$")
    @JsonView(Views.Admin.class)
    private String phone;

    @Min(0)
    @Max(5)
    private int rating;

    @Column(name = "license_number")
    @JsonView(Views.Admin.class)
    private String licenseNumber;

    @Column(name = "is_active")
    @JsonView(Views.Admin.class)
    @Builder.Default
    private boolean isActive = true;

    @JsonView(Views.Admin.class)
    @Builder.Default
    private boolean deletionRequested = false;

    @JsonView(Views.Public.class)
    private String role;

    @Email
    @JsonView(Views.Admin.class)
    private String email;

    @JsonView(Views.Admin.class)
    private String password;

    @Column(name = "created_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

}
