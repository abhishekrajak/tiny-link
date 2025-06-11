package com.abhishek.github.tinylink.dto;

import com.abhishek.github.tinylink.model.TinyLink;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TinyLinkDTO {
    private String tinyCode;
    private String redirectionLink;

    public TinyLinkDTO(TinyLink tinyLink){
        this.tinyCode = tinyLink.getTinyCode();
        this.redirectionLink = tinyLink.getRedirectionUrl();
    }
}
