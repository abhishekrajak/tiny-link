package com.abhishek.github.tinylink.controller;
import com.abhishek.github.tinylink.config.GoogleAuthConfig;
import com.abhishek.github.tinylink.dto.GoogleAuthRequest;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.abhishek.github.tinylink.service.UserService;
import com.google.api.client.json.Json;
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

    @Autowired
    AuthController(JwtTokenUtil jwtTokenUtil, GoogleIdTokenVerifier googleIdTokenVerifier,
                   GoogleAuthConfig googleAuthConfig, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
        this.googleAuthConfig = googleAuthConfig;
        this.userRepository = userRepository;
    }

    @GetMapping("/code/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestParam("code") String code, HttpServletResponse response) {
        try {
            // Verify Google ID token
//            GoogleIdToken.Payload payload = verifyGoogleToken(request.getToken());

//            https://accounts.google.com/signin/oauth/consent?authuser=0&part=AJi8hAP_N16gLRKXCMK24BmnsMKpoR7NSPDyeZ1aVPQ_0PNXed46PIsCoLDzPQqPnoIiujdn3AZXE3SfU6aPDE4QgWwhN18-N7nSBwplJ5Lo45X_Anz_Y-5mBLptQ6kagk5183G8MLBUKlVI4rOGMymyrXUF88V8MN-Gwci-5xZv845NIrqsryiW9hw51NdxTk8H9Ouqw6gnZJtAY-ZYrq-fuMvWjm3dRa6eiMTV4nNV3Lzbs6FG4MQM7EP0YQ6VJkxbnW3Fg47MUO9OXCDw3zLMqHySbPar4E-w6y845faNglr6KEdg0rN5vLTc1JbSu0FOsmPZ1L_JVZZyobpYfaSFumLVEZg-vZQMCHKUpaaEM5FF95hNR10l_DeCbRS48Di4l0TYDTaNrJdsR-tlp3vPRVB-1_MtQhCUy6OVDSNK1qdxzEI5RDHfRe3baius0Ux0BoIYSGHQvMBXPpJAK5gKg9fUuqin0g&flowName=GeneralOAuthFlow&as=S-66855224%3A1752179926562046&client_id=785082594074-fb5dmiet9bnndjk2276sksjjtp4m6p8c.apps.googleusercontent.com&pli=1&rapt=AEjHL4PRThf4zvNHUrx64dODNazM3_79hnl1nmwMS-s9lengLQ5HCseQVzkvN5Kt8uT3TOr85zlQUN-9hqF50qMdlSwPB0SxmQ
            // Extract user info
//            String email = payload.getEmail();
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");



            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:3333/auth.html?code=" + code))
                    .build();


            // Check if user exists in your DB or create new user
//            User user = userService.findOrCreateUser(email, name, pictureUrl);
//            User user = new User();
//            user.setName(name);
//            user.setEmailId(email);
//
//            // Generate your custom JWT
//            String jwtToken = jwtTokenUtil.generateToken(user);

//            return ResponseEntity.ok(jwtToken);
        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return null;
    }

    @PostMapping("/account/create")
    public ResponseEntity<?> createAccount(@RequestBody GoogleAuthRequest googleAuthRequest) throws Exception {

        String code = googleAuthRequest.getToken();

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleAuthConfig.googleClientId);
        params.add("client_secret", googleAuthConfig.googleClientSecret);
        params.add("redirect_uri", "http://localhost:8080/login/oauth2/code/google");
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", request, String.class);

        // Parse response for access_token, etc.
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> body = objectMapper.readValue(response.getBody(), Map.class);

        String token = body.get("id_token").toString();
        GoogleIdToken.Payload payload = verifyGoogleToken(token);

        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user;
        Optional<User> userOptional = userRepository.findByEmailId(email);
        if (userOptional.isEmpty()){
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
            user =  userRepository.save(newUser);
        } else {
            user = userOptional.get();
        }

        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("name", name);

        return ResponseEntity.ok(map);
    }


    private GoogleIdToken.Payload verifyGoogleToken(String idToken) throws Exception {
        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
        if (googleIdToken == null) {
            throw new RuntimeException("Invalid Google ID token");
        }
        return googleIdToken.getPayload();
    }
}
