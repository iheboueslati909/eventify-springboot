package com.eventify.ms.integration;

import com.eventify.ms.dto.ticket.UpdateTicketRequest;
import com.eventify.ms.dto.ticket.TicketResponse;
import com.eventify.ms.enums.Currency;
import com.eventify.ms.enums.EventStatus;
import com.eventify.ms.enums.EventType;
import com.eventify.ms.enums.MusicGenre;
import com.eventify.ms.enums.TicketPurchaseStatus;
import com.eventify.ms.model.Club;
import com.eventify.ms.model.Concept;
import com.eventify.ms.model.Event;
import com.eventify.ms.model.Member;
import com.eventify.ms.model.Ticket;
import com.eventify.ms.model.TicketPurchase;
import com.eventify.ms.model.auth.User;
import com.eventify.ms.repository.*;
import com.eventify.ms.repository.auth.UserRepository;
import com.eventify.ms.service.TicketService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class TicketServiceIntegrationTest {

    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private TicketPurchaseRepository ticketPurchaseRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ConceptRepository conceptRepository;
    
    @Autowired
    private ClubRepository clubRepository;

    private UUID memberId;
    private UUID ticketId;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("test@eventify.com");
        user.setPasswordHash("hashed-pass");
        user.setRole("member");

        Member member = Member.builder()
                .firstName("Test")
                .lastName("User")
                .user(user)
                .build();

        user.setMember(member);
        userRepository.save(user);

        Concept concept = conceptRepository.save(
                Concept.builder()
                        .memberId(member.getId())
                        .title("Industrial Techno Collective")
                        .description("A creative collective focused on dark, industrial techno events.")
                        .genres(Set.of(MusicGenre.TECHNO))
                        .isDeleted(false)
                        .createdAt(OffsetDateTime.now())
                        .build()
        );

        Club club = clubRepository.save(
                Club.builder()
                        .name("Vault 89")
                        .address("89 Industrial Street, Berlin")
                        .capacity(1200)
                        .isDeleted(false)
                        .createdAt(OffsetDateTime.now())
                        .owners(Set.of(member))
                        .build()
        );

        Event event = eventRepository.save(
                Event.builder()
                        .title("Techno Warehouse Night")
                        .description("An underground techno experience featuring top DJs and visual artists.")
                        .startDate(OffsetDateTime.now().plusMonths(2))
                        .endDate(OffsetDateTime.now().plusMonths(2).plusHours(8))
                        .location("Berlin Warehouse District")
                        .type(EventType.FESTIVAL)
                        .status(EventStatus.PUBLISHED)
                        .conceptId(concept.getId())
                        .clubId(club.getId())
                        .isDeleted(false)
                        .createdAt(OffsetDateTime.now())
                        .build()
        );

        ticket = ticketRepository.save(
                Ticket.builder()
                        .quantity(10)
                        .reservedCount(0)
                        .price(BigDecimal.valueOf(50))
                        .currency(Currency.USD)
                        .creator(member)
                        .event(event)
                        .name("General Admission")
                        .createdAt(OffsetDateTime.now())
                        .build()
        );

        ticketId = ticket.getId();
        memberId = member.getId();
    }

    @Test
    void updateTicket_ShouldSucceed_WhenNoTicketPurchaseExists() {
        // Arrange
        UpdateTicketRequest updateRequest = new UpdateTicketRequest(
                BigDecimal.valueOf(75),
                "VIP Ticket",
                15,
                Currency.USD
        );

        // Act
        TicketResponse response = ticketService.updateTicket(ticketId, updateRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.price()).isEqualByComparingTo(BigDecimal.valueOf(75));
        assertThat(response.name()).isEqualTo("VIP Ticket");
        assertThat(response.quantity()).isEqualTo(15);
        assertThat(response.currency()).isEqualTo(Currency.USD);

        // Verify in database
        Ticket updatedTicket = ticketRepository.findById(ticketId)
                .orElseThrow();
        assertThat(updatedTicket.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(75));
        assertThat(updatedTicket.getName()).isEqualTo("VIP Ticket");
        assertThat(updatedTicket.getQuantity()).isEqualTo(15);
    }

    @Test
    void updateTicket_ShouldThrowException_WhenTicketPurchaseExistsAndPriceChanged() {
        // Arrange
        // Create a ticket purchase
        TicketPurchase purchase = TicketPurchase.builder()
                .ticket(ticketRepository.findById(ticketId).orElseThrow())
                .user(memberRepository.findById(memberId).orElseThrow())
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(50))
                .status(TicketPurchaseStatus.PAID)
                .createdAt(OffsetDateTime.now())
                .build();

        ticket.getTicketPurchases().add(purchase);
        purchase = ticketPurchaseRepository.save(purchase);

        UpdateTicketRequest updateRequest = new UpdateTicketRequest(
                BigDecimal.valueOf(75), // Changed price
                "General Admission",
                10,
                Currency.USD
        );

        // Act & Assert
        assertThatThrownBy(() -> ticketService.updateTicket(ticketId, updateRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot update ticket price with active purchases");

        // Verify the ticket wasn't updated
        Ticket unchangedTicket = ticketRepository.findById(ticketId)
                .orElseThrow();
        assertThat(unchangedTicket.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(unchangedTicket.getName()).isEqualTo("General Admission");
        assertThat(unchangedTicket.getQuantity()).isEqualTo(10);
    }
}