package com.ankit.rideshare.service;

import com.ankit.rideshare.exception.DriverNotFoundException;
import com.ankit.rideshare.repository.DriverProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DriverService {

    private final DriverProfileRepository driverProfileRepository;
    public DriverService(DriverProfileRepository driverProfileRepository) {
        this.driverProfileRepository = driverProfileRepository;
    }

    @Transactional
    public void updateLocation(Long driverId, double lat, double lng) {
        int updated = driverProfileRepository.updateLocation(driverId, lat, lng, Instant.now());
        if(updated == 0){
            throw new DriverNotFoundException(driverId);        }
    }
}