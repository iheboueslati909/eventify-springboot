package com.eventify.ms.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventify.ms.model.auth.RefreshToken;
import com.eventify.ms.model.auth.User;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByToken(String token);
  int deleteByUser(User user);
}
