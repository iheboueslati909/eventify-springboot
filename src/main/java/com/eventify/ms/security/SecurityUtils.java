package com.eventify.ms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    
    public String getCurrentUserJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        
        // Get the JWT token from the credentials
        Object credentials = authentication.getCredentials();
        if (credentials == null) {
            throw new IllegalStateException("No JWT token found in authentication");
        }
        
        return "Bearer " + credentials.toString();
    }
}