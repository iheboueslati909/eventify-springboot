package com.eventify.ms.repository;

import com.eventify.ms.model.TicketPurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TicketPurchaseRepository extends JpaRepository<TicketPurchase, UUID> {
    @Query("SELECT tp FROM TicketPurchase tp LEFT JOIN FETCH tp.ticket WHERE tp.id = :id")
    Optional<TicketPurchase> findByIdWithTicket(@Param("id") UUID id);

    @Query("SELECT tp FROM TicketPurchase tp LEFT JOIN FETCH tp.ticket")
    Page<TicketPurchase> findAllWithTicket(Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(tp.quantity), 0) FROM TicketPurchase tp WHERE tp.ticket.id = :ticketId AND tp.status != 'CANCELLED'")
    Integer getTotalPurchasedQuantityForTicket(@Param("ticketId") UUID ticketId);

    @Query("SELECT COALESCE(SUM(tp.quantity), 0) FROM TicketPurchase tp WHERE tp.ticket.event.id = :eventId AND tp.status != 'CANCELLED'")
    Integer getTotalPurchasedQuantityForEvent(@Param("eventId") UUID eventId);
}