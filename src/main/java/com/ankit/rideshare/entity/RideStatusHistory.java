package com.ankit.rideshare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "ride_status_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RideStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rideId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    @Column(nullable = false, updatable = false)
    private Instant changedAt;

    @PrePersist
    private void prePersist() {
        this.changedAt = Instant.now();
    }

}
