package com.abhishek.github.tinylink.controller;

import com.abhishek.github.tinylink.service.TinyLinkService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class TinyLinkController {

    TinyLinkService tinyLinkService;

    public TinyLinkController(TinyLinkService tinyLinkService) {
        this.tinyLinkService = tinyLinkService;
    }

    @GetMapping(value = "/{tinyCode}")
    public ResponseEntity<String> getTinyLink(@PathVariable String tinyCode){
        Optional<String> url = tinyLinkService.getRedirectionUrl(tinyCode);

        if (url.isEmpty()) return ResponseEntity.notFound().build();

        String urlValue = url.get();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, urlValue)
                .build();
    }
}
