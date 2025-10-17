package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.config.TinyLinkConfiguration;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.TinyLinkDTO;
import com.abhishek.github.tinylink.dto.TinyLinkGenerateRequestDTO;
import com.abhishek.github.tinylink.dto.TinyLinkResponseDTO;
import com.abhishek.github.tinylink.exception.TinyLinkException;
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

    public Optional<String> getRedirectionUrl(String tinyCode){
        List<TinyLink> tinyLinkList = tinyLinkRepository.findByTinyCode(tinyCode);

        if(tinyLinkList.isEmpty()){
            throw new TinyLinkException(tinyCodeNotFound,"Tiny Code is not created for this request.");
        }

        TinyLinkDTO tinyLinkDTO =  new TinyLinkDTO(tinyLinkList.get(INT_ZERO));
        return Optional.of(tinyLinkDTO.getRedirectionLink());
    }

    public boolean insertTinyLink(TinyLinkGenerateRequestDTO tinyLinkGenerateRequestDTO) throws Exception {
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
        if (user.getUserType() == User.UserType.SPECIAL){
            if (firstMatchingPrefix.isPresent()) {
                User prefixOwner = firstMatchingPrefix.get().getUser();

                if (prefixOwner != user){
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
                user, isCustom, BLANK);
        tinyLinkRepository.save(tinyLink);
        return true;
    }

    User getUserViaAuthentication() throws Exception {
        String userId = getUserIdFromToken();
        Optional<User> user = userRepository.findById(UUID.fromString(userId));

        if (user.isEmpty()){
            throw new Exception("User not found invalid UUID");
        }

        return user.get();
    }

    String getUserIdFromToken() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()){
            throw new Exception("User is not authenticated");
        }

        String userId = authentication.getName();

        return userId;
    }

    public List<TinyLinkResponseDTO> getAllTinyLinks() throws Exception {
        String userId = getUserIdFromToken();

        List<TinyLink> links =
                tinyLinkRepository.findByUserId(UUID.fromString(userId));

        return links.stream().map(item -> new TinyLinkResponseDTO(
                item.getTinyCode(), item.getRedirectionUrl(),
                item.isCustom(), item.getCreatedAt()
        )).collect(Collectors.toList());
    }
}
