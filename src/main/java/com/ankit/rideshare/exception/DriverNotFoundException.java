package com.ankit.rideshare.exception;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(Long driverId) {
        super("Driver not found: " + driverId);
    }
}