package com.abhishek.github.tinylink.util;

import com.abhishek.github.tinylink.config.TinyLinkConfiguration;
import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class UrlSecurityValidator {
    private final TinyLinkConfiguration config;

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");


    public void validate(String url) throws TinyLinkException {
        if (isSelfReferencing(url)) {
            throw new TinyLinkException(ApiErrorCodes.SELF_REFERENCING_URL, "URL cannot point back to this service");
        }
        if (isInternalNetwork(url)) {
            throw new TinyLinkException(ApiErrorCodes.INTERNAL_URL_FORBIDDEN, "Internal network URLs are not allowed");
        }
        validateProtocol(url);
    }

    private boolean isSelfReferencing(String url) {
        return url.contains(config.getApiBaseUrl());
    }

    private boolean isInternalNetwork(String url) {
        return url.contains("localhost") || url.contains("127.0.0.1");
    }

    private void validateProtocol(String url) {
        try {
            URI uri = URI.create(url);
            String scheme = uri.getScheme();

            if (scheme == null || !ALLOWED_SCHEMES.contains(scheme)){
                throw new TinyLinkException(ApiErrorCodes.INVALID_URL,
                        String.format("Only the following protocols are allowed : %s",
                                String.join(" ", ALLOWED_SCHEMES)));
            }

            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                throw new TinyLinkException(ApiErrorCodes.INVALID_URL, "URL must contain a valid host");
            }

            if (!isValidDomain(host)) {
                throw new TinyLinkException(ApiErrorCodes.INVALID_URL,
                        "Invalid domain format - must contain a valid TLD (e.g., .com, .org, .net)");
            }
        } catch (TinyLinkException e){
            throw e;
        } catch (Exception e) {
            throw new TinyLinkException(ApiErrorCodes.INVALID_URL, "Malformed URL provided");
        }
    }

    private boolean isValidDomain(String host) {
        if (host == null || host.isEmpty() || host.length() > 253) {
            return false;
        }

        String[] labels = host.split("\\.");
        if (labels.length < 2) {
            return false;
        }

        for (String label : labels) {
            if (label.isEmpty() || label.length() > 63) {
                return false;
            }
            if (!label.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]*[a-zA-Z0-9])?$")) {
                return false;
            }
        }

        return true;
    }
}
