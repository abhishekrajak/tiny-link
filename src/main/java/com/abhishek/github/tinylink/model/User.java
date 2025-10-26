package com.abhishek.github.tinylink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

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

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prefix> prefixes = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuthProviderEntity> authProviders = new HashSet<>();

    private Boolean registrationCompleted;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>(); // "ROLE_ADMIN", "ROLE_USER"

    public void addAuthProvider(AuthProviderEntity.AuthProvider provider, String providerUserId) {
        AuthProviderEntity authProvider = new AuthProviderEntity(this, provider, providerUserId);
        this.authProviders.add(authProvider);
    }

    public void removeAuthProvider(AuthProviderEntity.AuthProvider provider) {
        authProviders.removeIf(ap -> ap.getProvider() == provider);
    }

    public Optional<AuthProviderEntity> getAuthProvider(AuthProviderEntity.AuthProvider provider) {
        return authProviders.stream()
                .filter(ap -> ap.getProvider() == provider)
                .findFirst();
    }

    public boolean hasProvider(AuthProviderEntity.AuthProvider provider) {
        return authProviders.stream()
                .anyMatch(ap -> ap.getProvider() == provider);
    }

    public enum UserType {
        BASE,
        CORPORATE,
        SPECIAL
    }

    public enum UserRole {
        ROLE_USER,
        ROLE_ADMIN,
    }

}
