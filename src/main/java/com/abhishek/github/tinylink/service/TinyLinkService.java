package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.config.TinyLinkConfiguration;
import com.abhishek.github.tinylink.dto.TinyLinkDTO;
import com.abhishek.github.tinylink.dto.TinyLinkGenerateRequestDTO;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import com.abhishek.github.tinylink.model.TinyLink;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.TinyLinkRepository;
import com.abhishek.github.tinylink.util.UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TinyLinkService {

    TinyLinkRepository tinyLinkRepository;

    TinyLinkConfiguration tinyLinkConfiguration;

    @Autowired
    public TinyLinkService(TinyLinkRepository tinyLinkRepository, TinyLinkConfiguration tinyLinkConfiguration) {
        this.tinyLinkRepository = tinyLinkRepository;
        this.tinyLinkConfiguration = tinyLinkConfiguration;
    }

    public Optional<String> getRedirectionUrl(String tinyCode){
        List<TinyLink> tinyLinkList = tinyLinkRepository.findByTinyCode(tinyCode);

        if (tinyLinkList.isEmpty()) return Optional.empty();

        TinyLinkDTO tinyLinkDTO =  new TinyLinkDTO(tinyLinkList.get(0));
        return Optional.of(tinyLinkDTO.getRedirectionLink());
    }

    public boolean insertTinyLink(TinyLinkGenerateRequestDTO tinyLinkGenerateRequestDTO) {
        User user = tinyLinkGenerateRequestDTO.getUser();

        String tinyCode = Optional.ofNullable(tinyLinkGenerateRequestDTO.getTinyCode()).orElse("");
        if (user.getUserType() == User.UserType.BASE || tinyCode.isEmpty()) {
            tinyCode = UrlGenerator.generateShortCode(tinyLinkConfiguration.getTinyUrlCodeLength(),
                    tinyLinkConfiguration.getTinyLinkAllowedChars());
        }

        boolean tinyCodePrefixExists = tinyLinkRepository.existsPrefixConflict(tinyCode);
        boolean isCustom = true;

        // Check for prefix for BASE users
        if (tinyCodePrefixExists && user.getUserType() == User.UserType.BASE) {
            isCustom = false;
            int maxRetryCount = tinyLinkConfiguration.getShortCodeGenerationMaxRetryCount();
            for (int index = 0; index < maxRetryCount && tinyCodePrefixExists; index++) {
                tinyCode = UrlGenerator.generateShortCode(tinyLinkConfiguration.getTinyUrlCodeLength(),
                        tinyLinkConfiguration.getTinyLinkAllowedChars());
                tinyCodePrefixExists = tinyLinkRepository.existsPrefixConflict(tinyCode);
            }

            if (tinyCodePrefixExists) {
                throw new TinyLinkException(ApiErrorCodes.tinyCodeGenerationRetryFail, "Tiny Code Generation Retry Fail");
            }
        }

        // TODO for special and corporate user prefix can match so check if they are allowed that prefix if no then throw Exception

        // Check if the shortCode already present in db
        boolean tinyCodeAlreadyExists = tinyLinkRepository.existsTinyLinkByTinyCode(tinyCode);

        if (tinyCodeAlreadyExists) {
            throw new TinyLinkException(ApiErrorCodes.tinyCodeGenerationRetryFail, "This tinyCode is already taken, please try again");
        }

        TinyLink tinyLink = new TinyLink(tinyLinkGenerateRequestDTO.getTinyCode(), tinyLinkGenerateRequestDTO.getRedirectionLink(),
                user, isCustom, "");
        tinyLinkRepository.save(tinyLink);
        return true;
    }
}
