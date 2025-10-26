package com.eventify.ms.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "firstName is required")    
        String firstName,

        @NotBlank(message = "lastName is required")    
        String lastName,
    
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")    
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 3, message = "Password must be at least 3 characters")
        String password) {}
