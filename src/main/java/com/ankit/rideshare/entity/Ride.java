package com.ankit.rideshare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "rides")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder

public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long riderId;

    @Column(nullable = true)
    private Long driverId;

    @Column(nullable = false)
    private Double pickupLat;

    @Column(nullable = false)
    private Double pickupLng;

    @Column(nullable = false)
    private Double dropLat;

    @Column(nullable = false)
    private Double dropLng;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    @Column(nullable = true)
    private Double estimatedFare;

    @Column(nullable = true)
    private Double finalFare;

    @Column(nullable = false, updatable = false)
    private Instant requestedAt;

    @Column(nullable = true)
    private Instant completedAt;

    @Version
    private Long version;

    @PrePersist
    private void prePersist() {
        this.requestedAt = Instant.now();
        if (this.status == null) {
            this.status = RideStatus.REQUESTED;
        }
    }
}
