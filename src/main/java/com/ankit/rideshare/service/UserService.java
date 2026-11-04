package com.ankit.rideshare.service;

import com.ankit.rideshare.dto.RegisterRequest;
import com.ankit.rideshare.entity.DriverProfile;
import com.ankit.rideshare.entity.Role;
import com.ankit.rideshare.entity.User;
import com.ankit.rideshare.exception.InvalidCredentialsException;
import com.ankit.rideshare.exception.InvalidRequestException;
import com.ankit.rideshare.exception.UserAlreadyExistsException;
import com.ankit.rideshare.repository.DriverProfileRepository;
import com.ankit.rideshare.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DriverProfileRepository driverProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       DriverProfileRepository driverProfileRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.driverProfileRepository = driverProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(String phone, String password) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new InvalidCredentialsException());
        if(!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return user;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (request.role() != Role.RIDER && request.role() != Role.DRIVER) {
            throw new InvalidRequestException("Role must be RIDER or DRIVER");
        }
        if (request.role() == Role.DRIVER
                && (request.vehicleType() == null || request.vehicleType().isBlank())) {
            throw new InvalidRequestException("vehicleType is required for drivers");
        }
        if (userRepository.findByPhone(request.phone()).isPresent()) {
            throw new UserAlreadyExistsException(request.phone());
        }

        User user = User.builder()
                .name(request.name())
                .phone(request.phone())
                .role(request.role())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();
        user = userRepository.save(user);

        if (request.role() == Role.DRIVER) {
            driverProfileRepository.save(DriverProfile.builder()
                    .userId(user.getId())
                    .vehicleType(request.vehicleType())
                    .isAvailable(true)
                    .build());
        }
        return user;
    }
}