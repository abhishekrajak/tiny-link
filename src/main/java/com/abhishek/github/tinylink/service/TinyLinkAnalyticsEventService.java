package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.dto.TinyLinkAnalyticsEventDTO;
import com.abhishek.github.tinylink.model.TinyLinkAnalyticsEvent;
import com.abhishek.github.tinylink.repository.TinyLinkAnalyticsEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class TinyLinkAnalyticsEventService {

    private final TinyLinkAnalyticsEventRepository tinyLinkAnalyticsEventRepository;

    @Async(value = "taskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveEvent(TinyLinkAnalyticsEventDTO eventDTO) {
        preprocessDTO(eventDTO);

        TinyLinkAnalyticsEvent event = new TinyLinkAnalyticsEvent(
                eventDTO.getTinyCode(),
                eventDTO.getIpAddress(),
                eventDTO.getUserAgent(),
                eventDTO.getReferer()
        );

        tinyLinkAnalyticsEventRepository.save(event);
    }

    private void preprocessDTO(TinyLinkAnalyticsEventDTO dto) {
        if (dto.getIpAddress() == null) {
            dto.setIpAddress("");
        }
        if (dto.getUserAgent() == null) {
            dto.setUserAgent("");
        }
        if (dto.getReferer() == null) {
            dto.setReferer("");
        }
    }

}
