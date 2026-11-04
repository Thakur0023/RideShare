package com.ankit.rideshare.repository;

import com.ankit.rideshare.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {
    @Query("SELECT d FROM DriverProfile d WHERE d.isAvailable = true AND d.currentLat BETWEEN :minLat AND :maxLat AND d.currentLng BETWEEN :minLng AND :maxLng")
    List<DriverProfile> findAvailableInBoundingBox(@Param("minLat") double minLat, @Param("maxLat") double maxLat,
    @Param("minLng") double minLng, @Param("maxLng") double maxLng);

    @Modifying
    @Query("UPDATE DriverProfile d SET d.isAvailable = false WHERE d.userId = :driverId AND d.isAvailable = true")
    int claimDriver(@Param("driverId") Long driverId);

    @Modifying
    @Query("UPDATE DriverProfile d SET d.isAvailable = true WHERE d.userId = :driverId")
    int releaseDriver(@Param("driverId") Long driverId);

    @Modifying
    @Query("UPDATE DriverProfile d SET d.currentLat = :lat, d.currentLng = :lng, d.locationUpdatedAt = :now WHERE d.userId = :driverId")
    int updateLocation(@Param("driverId") Long driverId,
                       @Param("lat") double lat,
                       @Param("lng") double lng,
                       @Param("now") Instant now);
}
