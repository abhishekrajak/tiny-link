package com.abhishek.github.tinylink.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Response payload representing a shortened link's details")
public class TinyLinkResponseDTO {
    @Schema(description = "The short code associated with the tiny link", example = "abcdef")
    private String tinyCode;

    @Schema(description = "The destination URL that the tiny link redirects to", example = "https://www.google.com")
    private String redirectionLink;

    @Schema(description = "Indicates if the short code is a custom name created by the user", example = "true")
    private boolean isCustom;

    @Schema(description = "The timestamp of when the tiny link was created", example = "2026-07-10T12:00:00Z")
    private Instant createdAt;

    @Schema(description = "The remaining limit/count of times this link can be visited/used", example = "1000")
    private Long linkCountRemaining;

    @Schema(description = "The url with tinycode that you can share directly")
    private String tinyLink;

}
