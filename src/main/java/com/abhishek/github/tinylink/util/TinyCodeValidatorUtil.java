package com.abhishek.github.tinylink.util;

import com.abhishek.github.tinylink.config.TinyLinkConfiguration;
import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TinyCodeValidatorUtil {
    private final TinyLinkConfiguration config;

    public void validate(String tinyCode) throws TinyLinkException {
        if (tinyCode.isEmpty()){
            throw new TinyLinkException(ApiErrorCodes.INVALID_TINY_CODE, ApiErrorMessages.tinyCodeEmpty);
        }

        if (tinyCode.length() < config.getTinyUrlCodeMinLength() || tinyCode.length() > config.getTinyUrlCodeMaxLength()){
            throw new TinyLinkException(ApiErrorCodes.INVALID_TINY_CODE, String.format(ApiErrorMessages.tinyCodeLength,
                    config.getTinyUrlCodeMinLength(), config.getTinyUrlCodeMaxLength()));
        }
    }
}
