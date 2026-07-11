package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.config.TinyLinkConfiguration;
import com.abhishek.github.tinylink.dto.TinyLinkGenerateRequestDTO;
import com.abhishek.github.tinylink.dto.TinyLinkResponseDTO;
import com.abhishek.github.tinylink.model.LinkStatus;
import com.abhishek.github.tinylink.model.TinyLink;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.TinyLinkRepository;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.abhishek.github.tinylink.util.TinyCodeValidatorUtil;
import com.abhishek.github.tinylink.util.UrlGenerator;
import com.abhishek.github.tinylink.util.UrlSecurityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tiny Link Service Test")
class TinyLinkServiceTest {
    @Mock
    TinyLinkRepository tinyLinkRepository;

    @Mock
    UserRepository userRepository;

    @Spy
    TinyLinkConfiguration tinyLinkConfiguration;

    TinyLinkService tinyLinkService;

    UrlSecurityValidator urlSecurityValidator;

    TinyCodeValidatorUtil tinyCodeValidatorUtil;


    @BeforeEach
    void setup() {
        this.tinyLinkConfiguration.setTinyUrlCodeLength(7);
        this.tinyLinkConfiguration.setTinyLinkAllowedChars("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        this.tinyLinkConfiguration.setShortCodeGenerationMaxRetryCount(5);
        this.tinyLinkConfiguration.setBaseUserMaxLinks(100);
        this.tinyLinkConfiguration.setSpecialUserMaxLinks(200);
        this.tinyLinkConfiguration.setCorporateUserMaxLinks(300);
        this.tinyLinkConfiguration.setApiBaseUrl("http://localhost:8080");
        this.tinyLinkConfiguration.setTinyUrlCodeMinLength(7);
        this.tinyLinkConfiguration.setTinyUrlCodeMaxLength(20);

        this.urlSecurityValidator = new UrlSecurityValidator(tinyLinkConfiguration);

        this.tinyCodeValidatorUtil = new TinyCodeValidatorUtil(tinyLinkConfiguration);

        this.tinyLinkService = new TinyLinkService(
                tinyLinkRepository,
                userRepository,
                tinyLinkConfiguration,
                urlSecurityValidator,
                tinyCodeValidatorUtil
        );
    }


    @Test
    @DisplayName("Redirection URL check if correct tiny code is passed")
    void getRedirectionUrlIfCorrectTinyCode() {
        String tinyCode = "ABHI1331";
        String expectedUrl = "https://www.twitter.com";

        TinyLink tinyLink = new TinyLink(
                tinyCode,
                expectedUrl,
                null,
                false,
                "",
                LinkStatus.ACTIVE
        );

        when(TinyLinkServiceTest.this.tinyLinkRepository.findByTinyCode(tinyCode)).thenReturn(Optional.of(tinyLink));

        String actualResult = tinyLinkService.getRedirectionUrl(tinyCode);

        assertEquals(expectedUrl, actualResult);
        verify(TinyLinkServiceTest.this.tinyLinkRepository).findByTinyCode(tinyCode);
    }

    @Test
    @DisplayName("Redirection URL check if incorrect tiny code is passed")
    void getRedirectionUrlIfIncorrectTinyCode() {
        String tinyCode = "ABHI1331";
        String expectedUrl = "";

        TinyLink tinyLink = new TinyLink(
                tinyCode,
                expectedUrl,
                null,
                false,
                "",
                LinkStatus.ACTIVE
        );

        when(TinyLinkServiceTest.this.tinyLinkRepository.findByTinyCode(tinyCode)).thenReturn(Optional.of(tinyLink));

        String actualResult = tinyLinkService.getRedirectionUrl(tinyCode);

        assertEquals(expectedUrl, actualResult);
        verify(TinyLinkServiceTest.this.tinyLinkRepository).findByTinyCode(tinyCode);
    }


    @Test
    void insertTinyLink_success_generatesShortCode_andSaves() throws Exception {
        String userId = UUID.randomUUID().toString();
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn(userId);

        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);

        User user = new User();
        user.setUserId(UUID.fromString(userId));
        user.setUserType(User.UserType.SPECIAL);

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));

        try (MockedStatic<UrlGenerator> mockedGenerator = mockStatic(UrlGenerator.class)){

            String tinyCodeGenerated = "ABC1234";

            mockedGenerator.when(() -> UrlGenerator.generateShortCode(anyInt(), anyString()))
                    .thenReturn(tinyCodeGenerated);

            TinyLinkGenerateRequestDTO req = new TinyLinkGenerateRequestDTO(tinyCodeGenerated, "https://example.com");

            when(tinyLinkRepository.existsTinyLinkByTinyCode(tinyCodeGenerated)).thenReturn(false);
            when(tinyLinkRepository.countByUserId(user.getUserId())).thenReturn(0L);

            TinyLink persisted = new TinyLink(tinyCodeGenerated, req.getRedirectionLink(), user, false, "", LinkStatus.ACTIVE);
            when(tinyLinkRepository.save(any(TinyLink.class))).thenReturn(persisted);

            TinyLinkResponseDTO result = tinyLinkService.insertTinyLink(req);

            assertEquals(tinyCodeGenerated, result.getTinyCode());
            assertEquals("https://example.com", result.getRedirectionLink());
            verify(tinyLinkRepository).save(any(TinyLink.class));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

}