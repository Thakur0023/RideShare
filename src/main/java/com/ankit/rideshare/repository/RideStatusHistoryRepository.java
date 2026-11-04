package com.ankit.rideshare.repository;

import com.ankit.rideshare.entity.RideStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideStatusHistoryRepository extends JpaRepository<RideStatusHistory, Long> {
    List<RideStatusHistory> findByRideIdOrderByChangedAtAsc(Long rideId);
}
