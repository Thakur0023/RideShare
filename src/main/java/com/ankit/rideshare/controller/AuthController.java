package com.ankit.rideshare.controller;

import com.ankit.rideshare.dto.LoginRequest;
import com.ankit.rideshare.dto.LoginResponse;
import com.ankit.rideshare.dto.RegisterRequest;
import com.ankit.rideshare.dto.UserResponse;
import com.ankit.rideshare.entity.User;
import com.ankit.rideshare.service.JwtService;
import com.ankit.rideshare.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));

    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.phone(), request.password());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token, UserResponse.from(user)));
    }
}