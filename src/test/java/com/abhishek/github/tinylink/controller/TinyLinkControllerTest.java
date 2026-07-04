package com.abhishek.github.tinylink.controller;

import com.abhishek.github.tinylink.service.TinyLinkAnalyticsEventService;
import com.abhishek.github.tinylink.service.TinyLinkService;
import com.abhishek.github.tinylink.util.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void test() throws Exception{

        String tinyCode = "ABCD1331";

        when(tinyLinkService.getRedirectionUrl(tinyCode)).thenReturn("");

        mockMvc.perform(get("/" + tinyCode))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", "/error/link-not-found.html"));
    }


}