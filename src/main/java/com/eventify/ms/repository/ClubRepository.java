package com.eventify.ms.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventify.ms.model.Club;

public interface ClubRepository extends JpaRepository<Club, UUID> {}
