package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.config.TinyLinkConfiguration;
import com.abhishek.github.tinylink.dto.*;
import com.abhishek.github.tinylink.exception.AccessDeniedException;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import com.abhishek.github.tinylink.model.LinkStatus;
import com.abhishek.github.tinylink.model.TinyLink;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.TinyLinkRepository;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.abhishek.github.tinylink.util.TinyCodeValidatorUtil;
import com.abhishek.github.tinylink.util.UrlGenerator;
import com.abhishek.github.tinylink.util.UrlSecurityValidator;
import io.opencensus.trace.Link;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.abhishek.github.tinylink.constant.StringConstants.BLANK;


@Service
@AllArgsConstructor
public class TinyLinkService {

    private final TinyLinkRepository tinyLinkRepository;

    private final UserRepository userRepository;

    private final TinyLinkConfiguration tinyLinkConfiguration;

    private final UrlSecurityValidator urlSecurityValidator;

    private final TinyCodeValidatorUtil tinyCodeValidatorUtil;

    private final UserContextService userContextService;

    @Transactional(readOnly = true)
    @Cacheable(value = "redirectionUrls", key = "#tinyCode")
    public String getRedirectionUrl(String tinyCode) {
        Optional<TinyLink> tinyLink = tinyLinkRepository.findByTinyCode(tinyCode);

        if (tinyLink.isEmpty()) {
            return BLANK;
        }

        TinyLinkDTO tinyLinkDTO = new TinyLinkDTO(tinyLink.get());
        return tinyLinkDTO.getRedirectionLink();
    }

    @Transactional
    public TinyLinkResponseDTO insertTinyLink(TinyLinkGenerateRequestDTO tinyLinkGenerateRequestDTO) throws Exception {
        urlSecurityValidator.validate(tinyLinkGenerateRequestDTO.getRedirectionLink());

        User user = getUserViaAuthentication();

        String tinyCode = Optional.ofNullable(tinyLinkGenerateRequestDTO.getTinyCode()).orElse(BLANK);
        if (tinyCode.isEmpty()) {
            tinyCode = UrlGenerator.generateShortCode(tinyLinkConfiguration.getTinyUrlCodeLength(),
                    tinyLinkConfiguration.getTinyLinkAllowedChars());
        }

        // This will throw exception if tinyCode does not follow rules
        tinyCodeValidatorUtil.validate(tinyCode);

        // Check if the shortCode already present in db
        boolean tinyCodeAlreadyExists = tinyLinkRepository.existsTinyLinkByTinyCode(tinyCode);

        if (tinyCodeAlreadyExists) {
            throw new TinyLinkException(ApiErrorCodes.tinyCodeGenerationRetryFail, "This tinyCode is already taken, please try with some other value");
        }

        // Check link limit for user
        long currentLinkCount = tinyLinkRepository.countByUserId(user.getUserId());
        int maxLinks = getMaxLinksForUserType(user.getUserType());

        if (currentLinkCount >= maxLinks) {
            throw new TinyLinkException(ApiErrorCodes.tinyCodeCountExceeded,
                    "You have reached the maximum limit of " + maxLinks + " links");
        }

        TinyLink tinyLink = new TinyLink(tinyCode, tinyLinkGenerateRequestDTO.getRedirectionLink(),
                user, true, BLANK, LinkStatus.ACTIVE);
        TinyLink savedTinyLink = tinyLinkRepository.save(tinyLink);

        return new TinyLinkResponseDTO(savedTinyLink.getTinyCode(), savedTinyLink.getRedirectionUrl(),
                savedTinyLink.isCustom(), savedTinyLink.getCreatedAt(), maxLinks - currentLinkCount,
                String.format("%s/%s", tinyLinkConfiguration.getApiBaseUrl(), savedTinyLink.getTinyCode()));
    }

    User getUserViaAuthentication() throws Exception {
        String userId = userContextService.getUserIdFromToken();
        Optional<User> user = userRepository.findById(UUID.fromString(userId));

        if (user.isEmpty()) {
            throw new TinyLinkException(ApiErrorCodes.userNotFound, "User not found");
        }

        return user.get();
    }


    @Transactional(readOnly = true)
    public List<TinyLinkResponseDTO> getAllTinyLinks() throws Exception {
        String userId = userContextService.getUserIdFromToken();

        List<TinyLink> links =
                tinyLinkRepository.findByUserId(UUID.fromString(userId), LinkStatus.ACTIVE.name());

        return links.stream().map(item -> new TinyLinkResponseDTO(
                item.getTinyCode(), item.getRedirectionUrl(),
                item.isCustom(), item.getCreatedAt(), null,
                String.format("%s/%s", tinyLinkConfiguration.getApiBaseUrl(), item.getTinyCode())
        )).collect(Collectors.toList());
    }

    public boolean updateTinyLink(TinyLinkUpdateRequestDTO tinyLinkUpdateRequestDTO) throws Exception {
        urlSecurityValidator.validate(tinyLinkUpdateRequestDTO.getRedirectionLink());

        String userId = userContextService.getUserIdFromToken();

        Optional<TinyLink> tinyLink =
                tinyLinkRepository.findByTinyCode(tinyLinkUpdateRequestDTO.getTinyCode());

        if (tinyLink.isEmpty()) {
            throw new TinyLinkException(ApiErrorCodes.TINY_LINK_NOT_FOUND, "Tiny Link Not Found");
        }

        if (!tinyLink.get().getUser().getUserId().toString().equals(userId)) {
            throw new AccessDeniedException("You are not the real user for this tiny link");
        }

        tinyLink.get().updateDetails(tinyLinkUpdateRequestDTO.getRedirectionLink());

        tinyLinkRepository.save(tinyLink.get());

        return true;

    }

    @Transactional
    public boolean updateTinyLinkStatus(TinyLinkStatusUpdateRequestDTO dto) throws Exception {

        String userId = userContextService.getUserIdFromToken();

        int updated = tinyLinkRepository.updateTinyLinkStatus(
                UUID.fromString(userId),
                dto.getTinyCode(),
                LinkStatus.INACTIVE.name()
        );

        if (updated == 0) {
            throw new TinyLinkException("LINK_NOT_FOUND", "Tiny Link not found or access denied");
        }

        return true;

    }

    private int getMaxLinksForUserType(User.UserType userType) {
        return switch (userType) {
            case BASE -> tinyLinkConfiguration.getBaseUserMaxLinks();
            case SPECIAL -> tinyLinkConfiguration.getSpecialUserMaxLinks();
            case CORPORATE -> tinyLinkConfiguration.getCorporateUserMaxLinks();
        };
    }

    @Transactional
    public int disableAllLinksForAUser(UUID userId, String status) throws Exception {
        return tinyLinkRepository.updateAllTinyLinkStatusForAUser(userId, status);
    }
}
