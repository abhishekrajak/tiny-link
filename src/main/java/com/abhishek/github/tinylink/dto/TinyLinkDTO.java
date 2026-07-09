package com.abhishek.github.tinylink.dto;

import com.abhishek.github.tinylink.model.TinyLink;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Data Transfer Object for high-level tiny link representations")
public class TinyLinkDTO {
    @Schema(description = "The short code associated with the tiny link", example = "abcdef")
    private String tinyCode;

    @Schema(description = "The destination URL that the tiny link redirects to", example = "https://www.google.com")
    private String redirectionLink;

    public TinyLinkDTO(TinyLink tinyLink){
        this.tinyCode = tinyLink.getTinyCode();
        this.redirectionLink = tinyLink.getRedirectionUrl();
    }
}
