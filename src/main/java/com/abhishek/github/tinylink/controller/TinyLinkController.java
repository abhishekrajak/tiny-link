package com.abhishek.github.tinylink.controller;

import com.abhishek.github.tinylink.annotation.RateLimited;
import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.*;
import com.abhishek.github.tinylink.service.TinyLinkAnalyticsEventService;
import com.abhishek.github.tinylink.service.TinyLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "Tiny Link Controller", description = "Endpoints for creating, updating, managing, and redirecting tiny links")
public class TinyLinkController {

    private final TinyLinkService tinyLinkService;

    private final TinyLinkAnalyticsEventService tinyLinkAnalyticsEventService;

    @Operation(
            summary = "Create a new tiny link",
            description = "Generates a short, unique code for the provided destination URL."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tiny link created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TinyLinkResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "BearerAuth")
    @RateLimited(policyKey = "createLink")
    @PostMapping(value = "/api/v1/tiny-link")
    public ApiResponse<?> addTinyLink(@RequestBody @Valid TinyLinkGenerateRequestDTO tinyLinkGenerateRequest) throws Exception {
        TinyLinkResponseDTO savedTinyLink = tinyLinkService.insertTinyLink(tinyLinkGenerateRequest);
        if (savedTinyLink != null) {
            return ApiResponse.success(savedTinyLink);
        } else {
            return new ApiResponse<>(ApiErrorCodes.unknownErrorCode, ApiErrorMessages.unknownErrorMessage, null);
        }
    }

    @Operation(
            summary = "Update destination URL",
            description = "Updates the destination URL associated with an existing tiny link."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tiny link URL updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "BearerAuth")
    @PatchMapping(value = "/api/v1/tiny-link/url")
    public ApiResponse<?> updateTinyLink(@RequestBody @Valid TinyLinkUpdateRequestDTO tinyLinkUpdateRequestDTO) throws Exception {
        boolean isSuccess = tinyLinkService.updateTinyLink(tinyLinkUpdateRequestDTO);
        if (isSuccess) {
            return ApiResponse.success("Tiny Link Updated Successfully");
        }
        throw new Exception();
    }

    @Operation(
            summary = "Redirect to target URL",
            description = "Redirects to the original URL mapped to the provided short code, or to the error page if not found."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "302",
                    description = "Redirection successful"
            )
    })
    @GetMapping(value = "/{tinyCode:[a-zA-Z0-9]{6,10}}")
    public ResponseEntity<String> getTinyLink(
            @Parameter(description = "The short code of the link to retrieve", required = true, example = "ABHI1331")
            @PathVariable String tinyCode,
            HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();

        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");

        TinyLinkAnalyticsEventDTO eventDTO = new TinyLinkAnalyticsEventDTO(
                tinyCode,
                ipAddress,
                userAgent,
                referer
        );

        String url = tinyLinkService.getRedirectionUrl(tinyCode);

        if (url == null || url.isEmpty()) {
            tinyLinkAnalyticsEventService.saveEvent(eventDTO);

            return ResponseEntity.status(302)
                    .location(URI.create("/error/link-not-found.html"))
                    .build();
        }

        tinyLinkAnalyticsEventService.saveEvent(eventDTO);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    @GetMapping(value = "/demo/{tinyCode:[a-zA-Z0-9]{6,10}}")
    public ApiResponse<?> getTinyLinkForDemo(
            @Parameter(description = "The short code of the link to retrieve", required = true, example = "ABHI1331")
            @PathVariable String tinyCode) {

        String url = tinyLinkService.getRedirectionUrl(tinyCode);

        if (url == null || url.isEmpty()) {
            return new ApiResponse<>(ApiErrorCodes.INVALID_TINY_CODE, "No URL with this tinyCode", null);
        } else {
            return ApiResponse.success(url);
        }

    }

    @Operation(
            summary = "Retrieve all tiny links",
            description = "Retrieves a list of all tiny links stored in the system."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all tiny links",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @SecurityRequirement(name = "BearerAuth")
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

    @Operation(
            summary = "Deactivate tiny link status",
            description = "Deactivates the status of an existing tiny link using the provided request details."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tiny link status updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(mediaType = "application/json")
            )
    })
    @SecurityRequirement(name = "BearerAuth")
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
