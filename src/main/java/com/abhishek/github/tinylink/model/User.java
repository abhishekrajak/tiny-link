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

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prefix> prefixes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AuthProviderEntity> authProviders = new HashSet<>();

    private Boolean registrationCompleted;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    )
    @Column(name = "roles", length = 20)
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
