package com.eventify.ms.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface UserRepository extends JpaRepository<com.eventify.ms.model.auth.User, UUID> {
  Optional<com.eventify.ms.model.auth.User> findByEmail(String email);
  boolean existsByEmail(String email);
}
