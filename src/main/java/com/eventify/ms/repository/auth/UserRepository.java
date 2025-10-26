package com.eventify.ms.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<com.eventify.ms.model.auth.User, Long> {
  Optional<com.eventify.ms.model.auth.User> findByEmail(String email);
  boolean existsByEmail(String email);
}
