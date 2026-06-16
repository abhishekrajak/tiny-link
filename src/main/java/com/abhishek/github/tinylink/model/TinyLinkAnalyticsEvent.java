package com.abhishek.github.tinylink.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "tiny_link_analytics_event")
public class TinyLinkAnalyticsEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String tinyCode;

    @Column(name = "ip_address", updatable = false)
    private String ipAddress;

    @Column(name = "time_stamp", nullable = false, updatable = false)
    private Instant timeStamp;

    @Column(updatable = false)
    private String userAgent;

    @Column
    private String referer;

    public TinyLinkAnalyticsEvent(String tinyCode, String ipAddress,
                                  String userAgent, String referer) {
        this.tinyCode = tinyCode;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.referer = referer;
        this.timeStamp = Instant.now();
    }
}