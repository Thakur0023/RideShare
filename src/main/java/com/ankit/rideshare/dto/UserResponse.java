package com.ankit.rideshare.dto;

import com.ankit.rideshare.entity.Role;
import com.ankit.rideshare.entity.User;

public record UserResponse(Long id, String name, String phone, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getPhone(), user.getRole());
    }
}