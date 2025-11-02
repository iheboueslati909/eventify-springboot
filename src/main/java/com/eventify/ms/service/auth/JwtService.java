package com.eventify.ms.service.auth;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.eventify.ms.exception.InvalidTokenException;
import com.eventify.ms.model.auth.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRY = 1000 * 60 * 15; // 15 min
    private static final long REFRESH_TOKEN_EXPIRY = 1000 * 60 * 60 * 24 * 7; // 7 days

    private Key signingKey;

    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalStateException("jwt.secret must be configured");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("jwt.secret must be valid Base64", e);
        }
    }

    public String generateToken(User user) {
        if (user == null || !StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("User and email must not be null");
        }
        
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        if (user == null || !StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("User and email must not be null");
        }
        
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String refreshAccessToken(String refreshToken, User user) {
        if (user == null || !StringUtils.hasText(user.getEmail())) {
            throw new IllegalArgumentException("User and email must not be null");
        }
        
        Optional<String> email = extractEmail(refreshToken);
        if (email.isEmpty()) {
            throw new InvalidTokenException("Cannot refresh token: token is invalid");
        }
        
        if (!email.get().equals(user.getEmail())) {
            throw new InvalidTokenException("Token email does not match user");
        }
        
        return generateToken(user);
    }

    public Optional<String> extractEmail(String token) {
        return extractAllClaims(token)
                .map(Claims::getSubject);
    }

    public boolean isTokenValid(String token) {
        return extractAllClaims(token).isPresent();
    }

    private Optional<Claims> extractAllClaims(String token) {
        if (!StringUtils.hasText(token)) {
            log.debug("Token validation failed: token is null or empty");
            return Optional.empty();
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // Additional validation
            if (!StringUtils.hasText(claims.getSubject())) {
                log.warn("Token validation failed: missing subject");
                return Optional.empty();
            }
            
            return Optional.of(claims);
            
        } catch (ExpiredJwtException e) {
            log.debug("Token validation failed: token expired");
            return Optional.empty();
        } catch (UnsupportedJwtException e) {
            log.warn("Token validation failed: unsupported JWT", e);
            return Optional.empty();
        } catch (MalformedJwtException e) {
            log.warn("Token validation failed: malformed JWT", e);
            return Optional.empty();
        } catch (SignatureException e) {
            log.warn("Token validation failed: invalid signature", e);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            log.warn("Token validation failed: illegal argument", e);
            return Optional.empty();
        } catch (JwtException e) {
            log.error("Token validation failed: unexpected JWT exception", e);
            return Optional.empty();
        }
    }


}