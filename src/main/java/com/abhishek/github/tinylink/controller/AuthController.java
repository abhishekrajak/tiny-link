package com.abhishek.github.tinylink.controller;

import com.abhishek.github.tinylink.config.GoogleAuthConfig;
import com.abhishek.github.tinylink.dto.GoogleAuthRequest;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.abhishek.github.tinylink.service.AuthService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.abhishek.github.tinylink.service.UserService;
import com.google.api.client.json.Json;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.*;
import com.abhishek.github.tinylink.util.JwtTokenUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login/oauth2")
public class AuthController {
    private final JwtTokenUtil jwtTokenUtil;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final GoogleAuthConfig googleAuthConfig;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    AuthController(JwtTokenUtil jwtTokenUtil, GoogleIdTokenVerifier googleIdTokenVerifier,
                   GoogleAuthConfig googleAuthConfig, UserRepository userRepository,
                   AuthService authService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.googleAuthConfig = googleAuthConfig;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @GetMapping("/code/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestParam("code") String code, HttpServletResponse response) {
        URI uri = authService.getClientRedirectUrl(googleAuthConfig.clientRedirectUrl, code);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(uri)
                .build();
    }

    @PostMapping("/account/create")
    public ResponseEntity<?> createAccount(@RequestBody GoogleAuthRequest googleAuthRequest) throws Exception {

        String code = googleAuthRequest.getToken();

        String token = authService.getAccessToken(code, googleAuthConfig);

        GoogleIdToken.Payload payload = authService.verifyGoogleToken(token);

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user;
        Optional<User> userOptional = userRepository.findByEmailId(email);
        if (userOptional.isEmpty()) {
            User newUser = new User();
            newUser.setEmailId(email);
            newUser.setName(name);
            newUser.setUsername(email);
            newUser.setProvider(User.AuthProvider.google);
            newUser.setProviderId(payload.getSubject());
            newUser.setPassword("");
            newUser.setRegistrationCompleted(payload.getEmailVerified());
            newUser.setRoles(Collections.singleton("ROLE_USER"));
            newUser.setUserType(User.UserType.BASE);
            newUser.setCreatedAt(Instant.now());
            user = userRepository.save(newUser);
        } else {
            user = userOptional.get();
        }

        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("name", name);

        return ResponseEntity.ok(map);
    }



}
