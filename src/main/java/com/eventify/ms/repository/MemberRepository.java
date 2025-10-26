package com.eventify.ms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eventify.ms.model.Member;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    @Query("SELECT m FROM Member m " +
           "LEFT JOIN FETCH m.user " +
           "LEFT JOIN FETCH m.artistProfiles " +
           "WHERE m.id = :id")
    Optional<Member> findByIdWithRelationships(@Param("id") UUID id);
    
    @Query("SELECT DISTINCT m FROM Member m " +
           "LEFT JOIN FETCH m.user " +
           "WHERE m.isDeleted = false")
    Page<Member> findAllActiveWithUser(Pageable pageable);
    
    boolean existsByEmailAndIsDeletedFalse(String email);
    
    Optional<Member> findByUserIdAndIsDeletedFalse(UUID userId);
    
    @Query("SELECT DISTINCT m FROM Member m " +
           "LEFT JOIN FETCH m.user " +
           "LEFT JOIN FETCH m.artistProfiles " +
           "WHERE m.email = :email AND m.isDeleted = false")
    Optional<Member> findByEmailWithRelationships(@Param("email") String email);
    
    // For user search functionality
    @Query("SELECT m FROM Member m " +
           "LEFT JOIN FETCH m.user " +
           "WHERE LOWER(m.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(m.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "AND m.isDeleted = false")
    Page<Member> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);
}