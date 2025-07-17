package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.config.GoogleAuthConfig;
import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Service
public class AuthService {
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Autowired
    AuthService(GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

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

    public String getAccessToken(String code, GoogleAuthConfig googleAuthConfig) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", googleAuthConfig.googleClientId);
            params.add("client_secret", googleAuthConfig.googleClientSecret);
            params.add("redirect_uri", googleAuthConfig.redirectUri);
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://oauth2.googleapis.com/token", request, String.class);

            // Parse response for access_token, etc.
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> body = objectMapper.readValue(response.getBody(), new TypeReference<>() {
            });

            return body.get("id_token").toString();
        } catch (Exception e) {
            throw new TinyLinkException(
                    ApiErrorCodes.tokenFetchFailed,
                    "Unable to fetch provider id token"
            );
        }
    }

    public GoogleIdToken.Payload verifyGoogleToken(String idToken) throws Exception {
        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
        if (googleIdToken == null) {
            throw new TinyLinkException(ApiErrorCodes.unknownErrorCode, "Something went wrong");
        }
        return googleIdToken.getPayload();
    }

}
