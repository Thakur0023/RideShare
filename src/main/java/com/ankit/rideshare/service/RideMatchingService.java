package com.ankit.rideshare.service;

import com.ankit.rideshare.entity.DriverProfile;
import com.ankit.rideshare.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.ankit.rideshare.util.GeoUtils.haversineKm;

@Service
public class RideMatchingService {

    private static final double SEARCH_RADIUS_KM = 5.0;
    private final DriverProfileRepository driverProfileRepository;

    public RideMatchingService(DriverProfileRepository driverProfileRepository) {
        this.driverProfileRepository = driverProfileRepository;
    }

    @Transactional
    public Optional<DriverProfile> assignNearestDriver(double pickupLat, double pickupLng) {
        List<DriverProfile> candidates = findNearbyDriversSorted(pickupLat, pickupLng);

        for (DriverProfile driver : candidates) {
            int updated = driverProfileRepository.claimDriver(driver.getUserId());
            if (updated == 1) {
                return Optional.of(driver);
            }
        }
        return Optional.empty();
    }

    public List<DriverProfile> findNearbyDriversSorted(double pickupLat, double pickupLng) {
        double latDelta = SEARCH_RADIUS_KM / 111.0;
        double lngDelta = SEARCH_RADIUS_KM / (111.0 * Math.cos(Math.toRadians(pickupLat)));

        List<DriverProfile> candidates = driverProfileRepository.findAvailableInBoundingBox(
                pickupLat - latDelta,
                pickupLat + latDelta,
                pickupLng - lngDelta,
                pickupLng + lngDelta
        );

        return candidates.stream()
                .sorted(Comparator.comparingDouble(d -> haversineKm(pickupLat, pickupLng, d.getCurrentLat(), d.getCurrentLng())))
                .toList();
    }
}