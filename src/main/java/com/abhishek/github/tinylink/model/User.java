package com.abhishek.github.tinylink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @Column(nullable = false, name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID userId;

    @Column(nullable = false, unique = true)
    private String emailId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prefix> prefixes = new ArrayList<>();

    public enum UserType {
        BASE,
        CORPORATE,
        SPECIAL
    }

}
