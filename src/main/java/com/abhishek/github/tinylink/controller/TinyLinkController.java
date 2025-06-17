package com.abhishek.github.tinylink.controller;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.ApiResponse;
import com.abhishek.github.tinylink.dto.TinyLinkGenerateRequestDTO;
import com.abhishek.github.tinylink.service.TinyLinkService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import static com.abhishek.github.tinylink.constant.StringConstants.ApiPathConstant.*;


@RestController
@AllArgsConstructor
public class TinyLinkController {

    private final TinyLinkService tinyLinkService;

    @PostMapping(value = SLASH+API+SLASH+VERSION+TINY_LINK)
    public ApiResponse<?> addTinyLink(@RequestBody TinyLinkGenerateRequestDTO tinyLinkGenerateRequest) {
        boolean isSuccess = tinyLinkService.insertTinyLink(tinyLinkGenerateRequest);
        if (isSuccess) {
            return ApiResponse.success(null);
        } else {
            return new ApiResponse<>(ApiErrorCodes.unknownErrorCode, ApiErrorMessages.unknownErrorMessage, null);
        }
    }

    @GetMapping(value = SLASH + PATH_VARIABLE_TINY_CODE)
    public ResponseEntity<String> getTinyLink(@PathVariable String tinyCode) {
        Optional<String> url = tinyLinkService.getRedirectionUrl(tinyCode);

        if (url.isEmpty()) return ResponseEntity.notFound().build();

        String urlValue = url.get();
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, urlValue)
                .build();
    }
}
