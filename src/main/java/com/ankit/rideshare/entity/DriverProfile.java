package com.ankit.rideshare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "driver_profile")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class DriverProfile {

    @Id
    private Long userId;

    @Column(nullable = false)
    private String vehicleType;

    @Column(nullable = false)
    private boolean isAvailable;

    @Column(nullable = true)
    private Double currentLat;

    @Column(nullable = true)
    private Double currentLng;

    @Column(nullable = true, updatable = true)
    private Instant locationUpdatedAt;

    @Version
    private Long version;
}
