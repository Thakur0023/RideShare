package com.ankit.rideshare.service;

import com.ankit.rideshare.entity.DriverProfile;
import com.ankit.rideshare.entity.Ride;
import com.ankit.rideshare.entity.RideStatus;
import com.ankit.rideshare.entity.RideStatusHistory;
import com.ankit.rideshare.exception.ForbiddenActionException;
import com.ankit.rideshare.exception.RideNotFoundException;
import com.ankit.rideshare.repository.DriverProfileRepository;
import com.ankit.rideshare.repository.RideRepository;
import com.ankit.rideshare.repository.RideStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RideStatusHistoryRepository historyRepository;
    private final RideMatchingService matchingService;
    private final PricingService pricingService;
    private final DriverProfileRepository driverProfileRepository;

    public RideService(RideRepository rideRepository,
                       RideStatusHistoryRepository historyRepository,
                       RideMatchingService matchingService,
                       PricingService pricingService,
                       DriverProfileRepository driverProfileRepository) {
        this.rideRepository = rideRepository;
        this.historyRepository = historyRepository;
        this.matchingService = matchingService;
        this.pricingService = pricingService;
        this.driverProfileRepository = driverProfileRepository;
    }

    private void changeStatus(Ride ride, RideStatus target) {
        if (!ride.getStatus().canTransitionTo(target)) {
            throw new IllegalStateException("Cannot move ride from " + ride.getStatus() + " to " + target);
        }
        ride.setStatus(target);
        rideRepository.save(ride);
        historyRepository.save(RideStatusHistory.builder()
                .rideId(ride.getId())
                .status(target)
                .build());
    }

    @Transactional
    public Ride requestRide(Long riderId, double pickupLat, double pickupLng,
                            double dropLat, double dropLng) {
        double fare = pricingService.estimateFare(pickupLat, pickupLng, dropLat, dropLng);

        Ride ride = Ride.builder()
                .riderId(riderId)
                .pickupLat(pickupLat)
                .pickupLng(pickupLng)
                .dropLat(dropLat)
                .dropLng(dropLng)
                .estimatedFare(fare)
                .status(RideStatus.REQUESTED)
                .build();
        ride = rideRepository.save(ride);

        historyRepository.save(RideStatusHistory.builder()
                .rideId(ride.getId())
                .status(RideStatus.REQUESTED)
                .build());

        Optional<DriverProfile> driver = matchingService.assignNearestDriver(pickupLat, pickupLng);

        if (driver.isPresent()) {
            ride.setDriverId(driver.get().getUserId());
            changeStatus(ride, RideStatus.ACCEPTED);
        } else {
            changeStatus(ride, RideStatus.NO_DRIVER_FOUND);
        }
        return ride;
    }

    @Transactional
    public Ride updateStatus(Long rideId, RideStatus target, Long userId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));

        boolean isRider = ride.getRiderId().equals(userId);
        boolean isDriver = ride.getDriverId() != null && ride.getDriverId().equals(userId);

        if(!isRider && !isDriver) {
            throw new ForbiddenActionException("You are not part of this ride");
        }
        if(target != RideStatus.CANCELLED && !isDriver) {
            throw new ForbiddenActionException("Only the assigned driver can set status" + target);
        }
        changeStatus(ride, target);

        if (target == RideStatus.COMPLETED) {
            ride.setCompletedAt(Instant.now());
            ride.setFinalFare(ride.getEstimatedFare());
            rideRepository.save(ride);
        }
        if ((target == RideStatus.COMPLETED || target == RideStatus.CANCELLED) && ride.getDriverId() != null) {
            driverProfileRepository.releaseDriver(ride.getDriverId());
        }
        return ride;
    }
}