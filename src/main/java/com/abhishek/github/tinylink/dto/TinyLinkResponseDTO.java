package com.abhishek.github.tinylink.dto;

import com.abhishek.github.tinylink.model.TinyLink;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
public class TinyLinkResponseDTO {
    private String tinyCode;
    private String redirectionLink;
    private boolean isCustom;
    private Instant createdAt;

}
