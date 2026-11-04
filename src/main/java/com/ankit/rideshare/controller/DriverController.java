package com.ankit.rideshare.controller;

import com.ankit.rideshare.dto.LocationUpdateRequest;
import com.ankit.rideshare.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;
    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PatchMapping("/me/location")
    public ResponseEntity<Void> updateLocation(@AuthenticationPrincipal Long driverId,
                                               @RequestBody LocationUpdateRequest request) {
        driverService.updateLocation(driverId, request.lat(),  request.lng());
        return ResponseEntity.noContent().build();
    }
}