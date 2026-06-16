package com.abhishek.github.tinylink.repository;

import com.abhishek.github.tinylink.model.TinyLinkAnalyticsEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TinyLinkAnalyticsEventRepository extends JpaRepository<TinyLinkAnalyticsEvent, UUID> {

}
