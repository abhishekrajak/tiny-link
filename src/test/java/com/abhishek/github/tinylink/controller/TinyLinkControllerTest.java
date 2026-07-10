package com.abhishek.github.tinylink.controller;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.ApiResponse;
import com.abhishek.github.tinylink.dto.TinyLinkGenerateRequestDTO;
import com.abhishek.github.tinylink.dto.TinyLinkResponseDTO;
import com.abhishek.github.tinylink.service.TinyLinkAnalyticsEventService;
import com.abhishek.github.tinylink.service.TinyLinkService;
import com.abhishek.github.tinylink.util.JwtTokenUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TinyLinkController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
                com.abhishek.github.tinylink.config.SecurityConfig.class
        }
)
class TinyLinkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private TinyLinkService tinyLinkService;

    @MockitoBean
    private TinyLinkAnalyticsEventService tinyLinkAnalyticsEventService;

    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    ObjectMapper objectMapper;

    @Test()
    @DisplayName("When redirection url is not present then redirect to error page")
    void redirectionUrlWhenTinyCodeIsNotPresent() throws Exception{
        String tinyCode = "ABCD1331";

        when(tinyLinkService.getRedirectionUrl(tinyCode)).thenReturn("");

        mockMvc.perform(get("/" + tinyCode))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/error/link-not-found.html"));
    }

    @Test()
    @DisplayName("When redirection url is present then redirect to error page")
    void redirectionUrlWhenTinyCodeIsPresent() throws Exception{

        String tinyCode = "ABCD1331";

        when(tinyLinkService.getRedirectionUrl(tinyCode)).thenReturn("https://www.google.com");

        mockMvc.perform(get("/" + tinyCode))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "https://www.google.com"));
    }

    @Test
    @DisplayName("Create tiny link when service returns non-null object")
    void createTinySuccessCase() throws Exception {
        TinyLinkGenerateRequestDTO tinyLinkGenerateRequestDTO =
                new TinyLinkGenerateRequestDTO(
                        "ABHI1331",
                        "https://www.twitter.com"
                );

        TinyLinkResponseDTO tinyLinkResponseDTO = new TinyLinkResponseDTO("ABHI1331", "https://www.twitter.com",
                true, Instant.now(), 99L, "http://localhost:8080/ABHI1331");

        when(tinyLinkService.insertTinyLink(tinyLinkGenerateRequestDTO)).thenReturn(
            tinyLinkResponseDTO
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/tiny-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                tinyLinkGenerateRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseInString = response.getContentAsString();

        ApiResponse<TinyLinkResponseDTO> result = objectMapper.readValue(responseInString,
                new TypeReference<>() {});

        assertEquals(ApiErrorCodes.successCode, result.getErrorCode());
        assertEquals(ApiErrorMessages.successMessage, result.getErrorMessage());
        assertEquals(tinyLinkResponseDTO.getTinyCode(), result.getData().getTinyCode());
        assertEquals(tinyLinkResponseDTO.getRedirectionLink(), result.getData().getRedirectionLink());
    }

    @Test
    @DisplayName("Create tiny link when service returns null object")
    void createTinyFailureCase() throws Exception {
        TinyLinkGenerateRequestDTO tinyLinkGenerateRequestDTO =
                new TinyLinkGenerateRequestDTO(
                        "ABHI1331",
                        "https://www.twitter.com"
                );

        when(tinyLinkService.insertTinyLink(tinyLinkGenerateRequestDTO)).thenReturn(null);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/tiny-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                tinyLinkGenerateRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        String responseInString = response.getContentAsString();

        ApiResponse<TinyLinkResponseDTO> result = objectMapper.readValue(responseInString,
                new TypeReference<>() {});

        assertEquals(ApiErrorCodes.unknownErrorCode, result.getErrorCode());
        assertEquals(ApiErrorMessages.unknownErrorMessage, result.getErrorMessage());
        assertNull(result.getData());
    }


}