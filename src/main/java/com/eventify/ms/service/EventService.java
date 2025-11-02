package com.eventify.ms.service;

import com.eventify.ms.dto.event.CreateEventRequest;
import com.eventify.ms.enums.EventStatus;
import com.eventify.ms.model.*;
import com.eventify.ms.repository.ArtistProfileRepository;
import com.eventify.ms.repository.EventRepository;
import com.eventify.ms.repository.TimeTableSlotRepository;
import com.eventify.ms.repository.ConceptRepository;
import com.eventify.ms.repository.ClubRepository;
import com.eventify.ms.repository.TicketPurchaseRepository;
import com.eventify.ms.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.eventify.ms.dto.event.EventResponse;
import com.eventify.ms.dto.event.ArtistTimeConflict;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final TimeTableSlotRepository timeTableSlotRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final ConceptRepository conceptRepository;
    private final ClubRepository clubRepository;
    private final TicketPurchaseRepository ticketPurchaseRepository;
    private final MemberRepository memberRepository;

    public EventService(
            EventRepository eventRepository,
            TimeTableSlotRepository timeTableSlotRepository,
            ArtistProfileRepository artistProfileRepository,
            ConceptRepository conceptRepository,
            ClubRepository clubRepository,
            TicketPurchaseRepository ticketPurchaseRepository,
            MemberRepository memberRepository) {
        this.eventRepository = eventRepository;
        this.timeTableSlotRepository = timeTableSlotRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.conceptRepository = conceptRepository;
        this.clubRepository = clubRepository;
        this.ticketPurchaseRepository = ticketPurchaseRepository;
        this.memberRepository = memberRepository;
    }

    // --- Read operations ---
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        return eventRepository.findAllActiveWithTimeTables(pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID id) {
        Event event = eventRepository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
        return mapToResponse(event);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByConcept(UUID conceptId) {
        if (!conceptRepository.existsById(conceptId)) {
            throw new NoSuchElementException("Concept not found with id: " + conceptId);
        }
        return eventRepository.findByConceptIdWithRelationships(conceptId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // --- Update ---
    @Transactional
    public UUID updateEvent(UUID id, CreateEventRequest request, java.util.UUID userId) {
        Event event = eventRepository.findByIdWithRelationships(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));

        // Validate concept exists
        if (!conceptRepository.existsById(request.conceptId())) {
            throw new NoSuchElementException("Concept not found with id: " + request.conceptId());
        }

        // Validate date range
        validateDateRange(request.startDate(), request.endDate());

        // Update scalar fields
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setStartDate(request.startDate());
        event.setEndDate(request.endDate());
        event.setLocation(request.location());
        event.setType(request.type());
        event.setConceptId(request.conceptId());
        event.setStatus(EventStatus.PUBLISHED);
        event.setCreator(memberRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with id: " + userId)));
        // Replace timetables: clear and create new ones
        event.getTimetables().clear();
        List<TimeTable> timetables = createTimeTables(request, event);
        event.setTimetables(timetables);

        eventRepository.save(event);
        return event.getId();
    }

    @Transactional
    public UUID updateEvent(UUID id, CreateEventRequest request) {
        return updateEvent(id, request, null);
    }

    // --- Delete (soft) ---
    @Transactional
    public void deleteEvent(UUID id, java.util.UUID userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));
        event.setDeleted(true);
        eventRepository.save(event);
    }

    @Transactional
    public void cancelEvent(UUID id, java.util.UUID userId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id: " + id));

        if (event.isDeleted()) {
            throw new IllegalStateException("Event is already deleted with id: " + id);
        }

        // If there are any purchased (non-cancelled) tickets for this event, disallow
        // cancel
        Integer totalPurchased = ticketPurchaseRepository.getTotalPurchasedQuantityForEvent(id);
        if (totalPurchased != null && totalPurchased > 0) {
            throw new IllegalStateException("Event has purchased tickets and cannot be canceled");
        }

        // Ownership check: if event belongs to a club, only club owners may cancel
        if (event.getCreator().getId() != null) {
            UUID creatorId = event.getCreator().getId();
            if (!creatorId.equals(userId)) {
                throw new IllegalStateException("Only the event creator can cancel this event");
            }
        } else {
            throw new IllegalStateException("Event has no creator assigned, cannot verify ownership");
        }

        // Mark event as deleted (soft cancel)
        event.setDeleted(true);
        eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(UUID id) {
        deleteEvent(id, null);
    }

    private EventResponse mapToResponse(Event event) {
        List<UUID> ttIds = event.getTimetables().stream()
                .map(t -> t.getId())
                .toList();
        List<UUID> ticketIds = event.getTickets().stream()
                .map(t -> t.getId())
                .toList();
        return new EventResponse(
                event.getId(),
                event.getCreator() != null ? event.getCreator().getId() : null,
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate(),
                event.getLocation(),
                event.getType(),
                event.getStatus(),
                event.getConceptId(),
                event.isDeleted(),
                event.getCreatedAt(),
                event.getClubId(),
                ttIds,
                ticketIds);
    }

    @Transactional
    public UUID createEvent(CreateEventRequest request, UUID userId) {
        // Validate concept exists
        if (!conceptRepository.existsById(request.conceptId())) {
            throw new NoSuchElementException("Concept not found with id: " + request.conceptId());
        }

        Member creator = memberRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new NoSuchElementException("Creator not found with id: " + userId));

        // Validate date range
        validateDateRange(request.startDate(), request.endDate());

        // Create event
        Event event = Event.builder()
                .title(request.title())
                .creator(creator)
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .location(request.location())
                .type(request.type())
                .conceptId(request.conceptId())
                .status(EventStatus.PUBLISHED)
                .build();

        // Process timetables and validate artist availability
        List<TimeTable> timetables;
        try {
            timetables = createTimeTables(request, event); // <- assign outside
        } catch (IllegalStateException e) {
            throw e; // propagate known validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to create event due to: ", e);
        }

        // Link timetables to event
        event.setTimetables(timetables);

        // Persist event + cascading timetables & slots
        event = eventRepository.save(event);

        return event.getId();
    }

    private void validateDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        if (startDate.isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
    }

    private List<TimeTable> createTimeTables(CreateEventRequest request, Event event) {
        return request.timeTables().stream()
                .map(ttRequest -> {
                    TimeTable timeTable = TimeTable.builder()
                            .event(event)
                            .stageName(ttRequest.stageName())
                            .build();

                    List<TimeTableSlot> slots = createTimeTableSlots(ttRequest.slots(), timeTable);
                    timeTable.setSlots(slots);

                    return timeTable;
                })
                .collect(Collectors.toList());
    }

    private List<TimeTableSlot> createTimeTableSlots(
            List<CreateEventRequest.TimeTableSlotRequest> slotRequests,
            TimeTable timeTable) {

        // Collect all unique artists and validate their existence upfront
        Set<UUID> allArtistIds = slotRequests.stream()
                .flatMap(slot -> slot.artistIds().stream())
                .collect(Collectors.toSet());

        // Batch fetch all artist profiles
        Map<UUID, ArtistProfile> artistProfiles = artistProfileRepository.findAllById(allArtistIds)
                .stream()
                .collect(Collectors.toMap(ArtistProfile::getId, Function.identity()));

        // Check for missing artists
        if (allArtistIds.size() != artistProfiles.size()) {
            throw new NoSuchElementException("Artist profiles not foun");
        }

        // Group slots by time range to optimize conflict checking
        Map<TimeRange, List<CreateEventRequest.TimeTableSlotRequest>> slotsByTimeRange = slotRequests.stream()
                .collect(Collectors.groupingBy(slot -> new TimeRange(slot.startTime(), slot.endTime())));

        // Check conflicts for each time range
        for (Map.Entry<TimeRange, List<CreateEventRequest.TimeTableSlotRequest>> entry : slotsByTimeRange.entrySet()) {
            TimeRange timeRange = entry.getKey();
            Set<UUID> artistsInTimeRange = entry.getValue().stream()
                    .flatMap(slot -> slot.artistIds().stream())
                    .collect(Collectors.toSet());

            // Batch check for conflicts
            Set<UUID> conflictingArtists = timeTableSlotRepository.findArtistsWithConflicts(
                    artistsInTimeRange,
                    timeRange.start(),
                    timeRange.end());

            if (!conflictingArtists.isEmpty()) {
                List<ArtistTimeConflict> conflicts = timeTableSlotRepository.findDetailedConflictsForArtists(
                        conflictingArtists,
                        timeRange.start(),
                        timeRange.end());
                throw new IllegalStateException(formatDetailedConflicts(conflicts));
            }
        }

        // Create slots using the pre-fetched artist profiles
        return slotRequests.stream()
                .map(slotRequest -> {
                    Set<ArtistProfile> slotArtists = slotRequest.artistIds().stream()
                            .map(artistProfiles::get)
                            .collect(Collectors.toSet());

                    return TimeTableSlot.builder()
                            .startTime(slotRequest.startTime())
                            .endTime(slotRequest.endTime())
                            .title(slotRequest.title())
                            .artistProfiles(slotArtists)
                            .timetable(timeTable)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private record TimeRange(OffsetDateTime start, OffsetDateTime end) {
    }

    private String formatDetailedConflicts(List<ArtistTimeConflict> conflicts) {
        return conflicts.stream()
                .map(conflict -> String.format(
                        "Artist '%s' has a conflict: already scheduled at '%s' on stage '%s' " +
                                "during %s - %s",
                        conflict.artistName(),
                        conflict.eventTitle(),
                        conflict.stageName(),
                        conflict.startTime(),
                        conflict.endTime()))
                .collect(Collectors.joining("\n"));
    }
}