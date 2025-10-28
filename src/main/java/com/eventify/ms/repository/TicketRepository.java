package com.eventify.ms.repository;

import com.eventify.ms.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.event e " +
           "LEFT JOIN FETCH t.creator c " +
           "LEFT JOIN FETCH t.ticketPurchases tp " +
           "WHERE t.id = :id")
    Optional<Ticket> findByIdWithRelationships(@Param("id") UUID id);

    @Query("SELECT DISTINCT t FROM Ticket t")
    Page<Ticket> findAllWithRelationships(Pageable pageable);
}
