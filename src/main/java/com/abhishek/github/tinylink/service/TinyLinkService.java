package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.config.TinyLinkConfiguration;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.TinyLinkDTO;
import com.abhishek.github.tinylink.dto.TinyLinkGenerateRequestDTO;
import com.abhishek.github.tinylink.dto.TinyLinkResponseDTO;
import com.abhishek.github.tinylink.dto.TinyLinkUpdateRequestDTO;
import com.abhishek.github.tinylink.exception.AccessDeniedException;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import com.abhishek.github.tinylink.model.LinkStatus;
import com.abhishek.github.tinylink.model.Prefix;
import com.abhishek.github.tinylink.model.TinyLink;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.TinyLinkRepository;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.abhishek.github.tinylink.util.UrlGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.abhishek.github.tinylink.constant.ApiErrorCodes.tinyCodeNotFound;
import static com.abhishek.github.tinylink.constant.StringConstants.BLANK;
import static com.abhishek.github.tinylink.constant.StringConstants.NumericConstant.INT_ZERO;


@Service
@AllArgsConstructor
public class TinyLinkService {

    private final TinyLinkRepository tinyLinkRepository;

    private final UserRepository userRepository;

    private final TinyLinkConfiguration tinyLinkConfiguration;

    @Transactional(readOnly = true)
    public Optional<String> getRedirectionUrl(String tinyCode) {
        Optional<TinyLink> tinyLink = tinyLinkRepository.findByTinyCode(tinyCode);

        if (tinyLink.isEmpty()) {
            throw new TinyLinkException(tinyCodeNotFound, "Tiny Code is not created for this request.");
        }

        TinyLinkDTO tinyLinkDTO = new TinyLinkDTO(tinyLink.get());
        return Optional.of(tinyLinkDTO.getRedirectionLink());
    }

    @Transactional
    public TinyLinkResponseDTO insertTinyLink(TinyLinkGenerateRequestDTO tinyLinkGenerateRequestDTO) throws Exception {
        User user = getUserViaAuthentication();

        String tinyCode = Optional.ofNullable(tinyLinkGenerateRequestDTO.getTinyCode()).orElse(BLANK);
        if (user.getUserType() == User.UserType.BASE || tinyCode.isEmpty()) {
            tinyCode = UrlGenerator.generateShortCode(tinyLinkConfiguration.getTinyUrlCodeLength(),
                    tinyLinkConfiguration.getTinyLinkAllowedChars());
        }

        Optional<Prefix> firstMatchingPrefix = tinyLinkRepository.findFirstMatchingPrefix(tinyCode);
        boolean tinyCodePrefixExists = firstMatchingPrefix.isPresent();
        boolean isCustom = true;

        // Check for prefix for BASE users
        if (tinyCodePrefixExists && user.getUserType() == User.UserType.BASE) {
            isCustom = false;
            int maxRetryCount = tinyLinkConfiguration.getShortCodeGenerationMaxRetryCount();
            for (int index = INT_ZERO; index < maxRetryCount && tinyCodePrefixExists; index++) {
                tinyCode = UrlGenerator.generateShortCode(tinyLinkConfiguration.getTinyUrlCodeLength(),
                        tinyLinkConfiguration.getTinyLinkAllowedChars());
                firstMatchingPrefix = tinyLinkRepository.findFirstMatchingPrefix(tinyCode);
                tinyCodePrefixExists = firstMatchingPrefix.isPresent();
            }

            if (tinyCodePrefixExists) {
                throw new TinyLinkException(ApiErrorCodes.tinyCodeGenerationRetryFail, "Tiny Code Generation Retry Fail");
            }
        }

        // TODO for special and corporate user prefix can match so check if they are allowed that prefix if no then throw Exception
        if (user.getUserType() == User.UserType.SPECIAL) {
            if (firstMatchingPrefix.isPresent()) {
                User prefixOwner = firstMatchingPrefix.get().getUser();

                if (prefixOwner != user) {
                    throw new TinyLinkException(ApiErrorCodes.prefixBelongsToOtherUser, ApiErrorMessages.prefixBelongsToOtherUser);
                }
            }
        }

        // Check if the shortCode already present in db
        boolean tinyCodeAlreadyExists = tinyLinkRepository.existsTinyLinkByTinyCode(tinyCode);

        if (tinyCodeAlreadyExists) {
            throw new TinyLinkException(ApiErrorCodes.tinyCodeGenerationRetryFail, "This tinyCode is already taken, please try again");
        }

        TinyLink tinyLink = new TinyLink(tinyLinkGenerateRequestDTO.getTinyCode(), tinyLinkGenerateRequestDTO.getRedirectionLink(),
                user, isCustom, BLANK, LinkStatus.ACTIVE);
        TinyLink savedTinyLink = tinyLinkRepository.save(tinyLink);

        return new TinyLinkResponseDTO(savedTinyLink.getTinyCode(), savedTinyLink.getRedirectionUrl(),
                savedTinyLink.isCustom(), savedTinyLink.getCreatedAt());
    }

    User getUserViaAuthentication() throws Exception {
        String userId = getUserIdFromToken();
        Optional<User> user = userRepository.findById(UUID.fromString(userId));

        if (user.isEmpty()) {
            throw new Exception("User not found invalid UUID");
        }

        return user.get();
    }

    String getUserIdFromToken() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User is not authenticated");
        }

        String userId = authentication.getName();

        return userId;
    }

    @Transactional(readOnly = true)
    public List<TinyLinkResponseDTO> getAllTinyLinks() throws Exception {
        String userId = getUserIdFromToken();

        List<TinyLink> links =
                tinyLinkRepository.findByUserId(UUID.fromString(userId));

        return links.stream().map(item -> new TinyLinkResponseDTO(
                item.getTinyCode(), item.getRedirectionUrl(),
                item.isCustom(), item.getCreatedAt()
        )).collect(Collectors.toList());
    }

    public boolean updateTinyLink(TinyLinkUpdateRequestDTO tinyLinkUpdateRequestDTO) throws Exception {
        String userId = getUserIdFromToken();


        Optional<TinyLink> tinyLink =
                tinyLinkRepository.findByTinyCode(tinyLinkUpdateRequestDTO.getTinyCode());

        if (tinyLink.isEmpty()) {
            throw new TinyLinkException("XOXO123", "Tiny Link Not Found");
        }

        if (!tinyLink.get().getUser().getUserId().toString().equals(userId)) {
            throw new AccessDeniedException("You are not the real user for this tiny link");
        }

        tinyLink.get().updateDetails(tinyLinkUpdateRequestDTO.getRedirectionLink());

        tinyLinkRepository.save(tinyLink.get());

        return true;

    }
}
