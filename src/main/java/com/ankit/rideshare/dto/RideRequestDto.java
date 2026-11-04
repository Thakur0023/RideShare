package com.ankit.rideshare.dto;

public record RideRequestDto(
        double pickupLat,
        double pickupLng,
        double dropLat,
        double dropLng
) {}