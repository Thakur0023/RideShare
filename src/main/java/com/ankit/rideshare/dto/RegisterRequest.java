package com.ankit.rideshare.dto;

import com.ankit.rideshare.entity.Role;

public record RegisterRequest(String name, String phone, String password, Role role, String vehicleType) {}