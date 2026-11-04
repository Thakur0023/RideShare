package com.ankit.rideshare.service;

import com.ankit.rideshare.entity.DriverProfile;
import com.ankit.rideshare.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.ankit.rideshare.util.GeoUtils.haversineKm;


@Service
public class PricingService {

    private final DriverProfileRepository driverProfileRepository;

    public PricingService(DriverProfileRepository driverProfileRepository) {
        this.driverProfileRepository = driverProfileRepository;
    }
    private static final double BASE_FARE = 40.0;
    private static final double PER_KM_RATE = 12.0;

    public double estimateFare(double pickupLat, double pickupLng, double dropLat, double dropLng) {
        double distanceKM = haversineKm(pickupLat, pickupLng, dropLat, dropLng);
        double surgeMultiplier = computeSurgeMultiplier(pickupLat, pickupLng);
        return BASE_FARE + distanceKM * PER_KM_RATE * surgeMultiplier;
    }

    private double computeSurgeMultiplier(double lat, double lng) {
        double latDelta = 3.0 / 111.0;
        double lngDelta = 3.0 / (111.0 * Math.cos(Math.toRadians(lat)));

        List<DriverProfile> nearbyDrivers = driverProfileRepository.findAvailableInBoundingBox(
                lat - latDelta,
                lat + latDelta,
                lng - lngDelta,
                lng + lngDelta
        );
        if(nearbyDrivers.size() < 3) {
            return 1.5;
        }
        else {
            return 1.0;
        }
    }
}