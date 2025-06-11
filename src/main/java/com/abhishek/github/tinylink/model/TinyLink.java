package com.abhishek.github.tinylink.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "tiny_links")
@Getter
public class TinyLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 12)
    private String tinyCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String redirectionUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant expiresAt;

    @Column(nullable = false)
    private boolean isCustom;

    @Column(nullable = false)
    private String codeType;

    public TinyLink() {
    }

    public TinyLink(String tinyCode, String redirectionUrl,
                    User user, boolean isCustom, String codeType) {
        this.tinyCode = tinyCode;
        this.redirectionUrl = redirectionUrl;
        this.user = user;
        this.isCustom = isCustom;
        this.codeType = codeType;
        this.createdAt = Instant.now();
    }

}
