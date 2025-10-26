package com.eventify.ms.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventify.ms.dto.auth.AuthResponse;
import com.eventify.ms.dto.auth.LoginRequest;
import com.eventify.ms.dto.auth.RefreshTokenRequest;
import com.eventify.ms.dto.auth.RegisterRequest;
import com.eventify.ms.repository.auth.UserRepository;
import com.eventify.ms.service.auth.AuthService;
import com.eventify.ms.service.auth.JwtService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthController(
            JwtService jwtService,
            UserRepository userRepository,
            AuthService authService
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    // Basic null validation (though @Valid should cover most cases)
    if (request.email() == null || request.password() == null) {
        return ResponseEntity.badRequest().body("Email and password must be provided");
    }

    if (userRepository.findByEmail(request.email()).isPresent()) {
        return ResponseEntity.badRequest().body("Email already in use");
    }

    // Delegate to a proper service layer
    try {
        AuthResponse response = authService.registerUser(request);
        return ResponseEntity.ok(response);
    } catch (IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.loginUser(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login failed");
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String newAccessToken = jwtService.refreshAccessToken(request.refreshToken());
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
