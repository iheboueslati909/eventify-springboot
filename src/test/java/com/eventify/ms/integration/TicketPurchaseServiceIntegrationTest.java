package com.eventify.ms.integration;

import com.eventify.ms.dto.payment.PaymentSessionResponse;
import com.eventify.ms.dto.ticket.CreateTicketPurchaseRequest;
import com.eventify.ms.dto.ticket.CreateTicketPurchaseResponse;
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
import com.eventify.ms.repository.ClubRepository;
import com.eventify.ms.repository.ConceptRepository;
import com.eventify.ms.repository.EventRepository;
import com.eventify.ms.repository.MemberRepository;
import com.eventify.ms.repository.TicketPurchaseRepository;
import com.eventify.ms.repository.TicketRepository;
import com.eventify.ms.repository.auth.UserRepository;
import com.eventify.ms.security.SecurityUtils;
import com.eventify.ms.service.PaymentService;
import com.eventify.ms.service.TicketPurchaseService;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class TicketPurchaseServiceIntegrationTest {

        @Autowired
        private TicketPurchaseService ticketPurchaseService;
        @Autowired
        private TicketRepository ticketRepository;
        @Autowired
        private MemberRepository memberRepository;
        @Autowired
        private TicketPurchaseRepository ticketPurchaseRepository;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private EventRepository eventRepository;
        @Autowired
        private ConceptRepository conceptRepository;
        @Autowired
        private ClubRepository clubRepository;
        @Autowired
        private EntityManager entityManager;

        @MockitoBean
        private PaymentService paymentService;
        @MockitoBean
        private SecurityUtils securityUtils;

        private UUID memberId;
        private UUID ticketId;
        private UUID eventId;
        private UUID conceptId;

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
        userRepository.save(user); // cascades to Member

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
                        .owners(Set.of(member)) // assuming 'member' is the owner
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
                .clubId(club != null ? club.getId() : null)
                .isDeleted(false)
                .createdAt(OffsetDateTime.now())
                .build()
);

        Ticket ticket = ticketRepository.save(
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
        eventId = event.getId();
        conceptId = concept.getId();

    }

        @Test
        void createTicketPurchase_ShouldCreatePendingPaymentAndInitiatePayment() {
                if (!ticketRepository.findById(ticketId).isPresent()) { 
                        throw new IllegalStateException("Ticket not found in setup");
                }
                if (!memberRepository.existsById(memberId)) {
                        throw new IllegalStateException("Member not found in setup");
                }

                when(securityUtils.getCurrentUserJwt()).thenReturn("jwt-token");
                when(paymentService.initiatePaymentSession(
                                anyString(), any(), any(), any(), anyString(), anyString()))
                                .thenReturn(new PaymentSessionResponse("checkout-url", "payment-id-123"));



                var request = new CreateTicketPurchaseRequest(ticketId, memberId, 2, "credit_card");
                var response = ticketPurchaseService.createTicketPurchase(request);

                var savedPurchase = ticketPurchaseRepository.findById(response.ticketPurchaseId()).orElseThrow();

                assertThat(savedPurchase.getStatus()).isEqualTo(TicketPurchaseStatus.PENDING_PAYMENT);
                assertThat(savedPurchase.getPaymentId()).isEqualTo("payment-id-123");
                assertThat(savedPurchase.getCheckoutUrl()).isEqualTo("checkout-url");
                assertThat(savedPurchase.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));

                verify(paymentService).initiatePaymentSession(
                                "jwt-token",
                                savedPurchase.getId(),
                                memberId,
                                BigDecimal.valueOf(100),
                                "usd",
                                "credit_card");
        }

        @Test
        void createTicketPurchase_ShouldFail_WhenNotEnoughTickets() {
                // Arrange
                var request = new CreateTicketPurchaseRequest(ticketId, memberId, 20, "credit_card");

                // Act + Assert
                assertThatThrownBy(() -> ticketPurchaseService.createTicketPurchase(request))
                                .isInstanceOf(IllegalStateException.class)
                                .hasMessageContaining("Not enough tickets available");
        }

        @Test
        void createTicketPurchase_ShouldCancel_WhenPaymentFails() {
        // Arrange
        when(securityUtils.getCurrentUserJwt()).thenReturn("jwt-token");
        when(paymentService.initiatePaymentSession(any(), any(), any(), any(), anyString(), anyString()))
                .thenReturn(null);

        var request = new CreateTicketPurchaseRequest(ticketId, memberId, 1, "credit_card");

        // Act - CALL THE METHOD!
        CreateTicketPurchaseResponse response = ticketPurchaseService.createTicketPurchase(request);

        // Assert on response
        assertThat(response).isNotNull();
        assertThat(response.checkoutUrl()).isNull();
        assertThat(response.paymentId()).isNull();

        // Refresh or re-query the entity
        ticketPurchaseRepository.flush(); // Ensure save is flushed
        TicketPurchase purchase = ticketPurchaseRepository.findById(response.ticketPurchaseId())
                .orElseThrow(() -> new AssertionError("Purchase should exist"));

        // Assert status is CANCELLED
        assertThat(purchase.getStatus()).isEqualTo(TicketPurchaseStatus.CANCELLED);
        }
}
