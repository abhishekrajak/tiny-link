package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class AuthService {

    public URI getClientRedirectUrl(String clientUrl, String code) {
        try {
            URIBuilder uriBuilder = new URIBuilder(clientUrl);
            uriBuilder.addParameter("code", code);
            return uriBuilder.build();
        } catch (Exception e) {
            throw new TinyLinkException(
                    ApiErrorCodes.clientUrlException,
                    "Unable to generate redirect url"
            );
        }
    }

}
