package com.abhishek.github.tinylink.controller;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.*;
import com.abhishek.github.tinylink.service.TinyLinkService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@Validated
@AllArgsConstructor
public class TinyLinkController {

    private final TinyLinkService tinyLinkService;

    @PostMapping(value = "/api/v1/tiny-link")
    public ApiResponse<?> addTinyLink(@RequestBody @Valid TinyLinkGenerateRequestDTO tinyLinkGenerateRequest) throws Exception {
        TinyLinkResponseDTO savedTinyLink = tinyLinkService.insertTinyLink(tinyLinkGenerateRequest);
        if (savedTinyLink != null) {
            return ApiResponse.success(savedTinyLink);
        } else {
            return new ApiResponse<>(ApiErrorCodes.unknownErrorCode, ApiErrorMessages.unknownErrorMessage, null);
        }
    }

    @PatchMapping(value = "/api/v1/tiny-link/url")
    public ApiResponse<?> updateTinyLink(@RequestBody @Valid TinyLinkUpdateRequestDTO tinyLinkUpdateRequestDTO) throws Exception {
        boolean isSuccess = tinyLinkService.updateTinyLink(tinyLinkUpdateRequestDTO);
        if (isSuccess) {
            return ApiResponse.success("Tiny Link Updated Successfully");
        }
        throw new Exception();
    }

    @GetMapping(value = "/{tinyCode:[a-zA-Z0-9]{6,10}}")
    public ResponseEntity<String> getTinyLink(@PathVariable String tinyCode) {
        String url = tinyLinkService.getRedirectionUrl(tinyCode);

        if (url == null || url.isEmpty()) {
            return ResponseEntity.status(302)
                    .location(URI.create("/error/link-not-found.html"))
                    .build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    @GetMapping(value = "/api/v1/links")
    public ApiResponse<?> getTinyLinkTest() {
        try {
            List<TinyLinkResponseDTO> links = tinyLinkService.getAllTinyLinks();
            return ApiResponse.success(links);
        } catch (Exception e) {
            // Nothing to do add logs later
        }

        return new ApiResponse<>("XOXO", "NO DATA FOUND", null);
    }

    @PatchMapping(value = "/api/v1/tiny-link/status/deactivate")
    public ApiResponse<?> updateTinyLinkStatus(
            @Valid @RequestBody
            TinyLinkStatusUpdateRequestDTO dto) throws Exception {

        boolean result = tinyLinkService.updateTinyLinkStatus(dto);

        if (result){
            return ApiResponse.success(null);
        } else {
            return new ApiResponse<>(ApiErrorCodes.unknownErrorCode, ApiErrorMessages.unknownErrorMessage, null);
        }

    }
}
