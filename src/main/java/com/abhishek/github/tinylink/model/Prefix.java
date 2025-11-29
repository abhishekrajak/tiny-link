package com.abhishek.github.tinylink.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "prefixes", uniqueConstraints = @UniqueConstraint(columnNames = {"prefix"}))
@Getter
@NoArgsConstructor
public class Prefix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prefixId;

    @Column(nullable = false, unique = true, length = 20)
    private String prefix;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private ReservedFor reservedFor;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Prefix(String prefix, User user, ReservedFor reservedFor) {
        this.prefix = prefix;
        this.user = user;
        this.reservedFor = reservedFor;
        this.createdAt = Instant.now();
    }

    public enum ReservedFor {
        CORPORATE,
        SPECIAL
    }
}