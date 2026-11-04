package com.ankit.rideshare.controller;

import com.ankit.rideshare.dto.RideRequestDto;
import com.ankit.rideshare.entity.Ride;
import com.ankit.rideshare.entity.RideStatus;
import com.ankit.rideshare.service.RideService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public Ride requestRide(@AuthenticationPrincipal Long riderId,
                            @RequestBody RideRequestDto request) {
        return rideService.requestRide(
                riderId,
                request.pickupLat(),
                request.pickupLng(),
                request.dropLat(),
                request.dropLng()
        );
    }

    @PatchMapping("/{rideId}/status")
    public Ride updateStatus(@AuthenticationPrincipal Long userId,
                             @PathVariable Long rideId,
                             @RequestParam RideStatus target) {
        return rideService.updateStatus(rideId, target, userId);
    }
}