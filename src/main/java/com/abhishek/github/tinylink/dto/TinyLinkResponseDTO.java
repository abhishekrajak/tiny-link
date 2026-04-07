package com.abhishek.github.tinylink.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
@Setter
public class TinyLinkResponseDTO {
    private String tinyCode;
    private String redirectionLink;
    private boolean isCustom;
    private Instant createdAt;
    private Long linkCountRemaining;

}
