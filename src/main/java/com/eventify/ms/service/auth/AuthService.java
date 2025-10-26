package com.eventify.ms.service.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import com.eventify.ms.dto.auth.AuthResponse;
import com.eventify.ms.dto.auth.LoginRequest;
import com.eventify.ms.dto.auth.RegisterRequest;
import com.eventify.ms.model.Member;
import com.eventify.ms.model.auth.User;
import com.eventify.ms.repository.auth.UserRepository;


@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create User
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        // Create Member
        Member member = new Member();
        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setEmail(request.email());

        user.setMember(member); // handles both directions
        member.setUser(user);
        userRepository.save(user); // cascades save to member

        // JWT tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request) {
        // Step 1: Authenticate credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Step 2: Fetch user entity (with roles, if applicable)
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Step 3: Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Step 4: Return response DTO
        return new AuthResponse(accessToken, refreshToken);
    }
}
