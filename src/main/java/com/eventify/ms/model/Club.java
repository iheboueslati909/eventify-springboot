package com.eventify.ms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.*;

@Entity
@Table(name = "clubs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Club {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    // Owners: many-to-many between club and member
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "club_owners",
        joinColumns = @JoinColumn(name = "club_id"),
        inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> owners = new HashSet<>();
}