package com.ankit.rideshare.repository;

import com.ankit.rideshare.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByRiderIdOrderByRequestedAtDesc(Long riderId);
}
