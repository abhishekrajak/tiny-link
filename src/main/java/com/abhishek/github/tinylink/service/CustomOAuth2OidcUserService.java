package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class CustomOAuth2OidcUserService extends OidcUserService {

    private final UserRepository userRepo;

    public CustomOAuth2OidcUserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest request) {

                
        try {
            // 1. Load the user from the OAuth2 provider
            OidcUser oauthUser = super.loadUser(request);
            log.info("OAuth2 User loaded successfully");
            
            // 2. Extract claims
            Map<String, Object> attributes = oauthUser.getAttributes();
            log.info("User attributes: {}", attributes);
            
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String providerId = (String) attributes.get("sub");
            
            if (email == null) {
                log.error("Email not found in OAuth2 user attributes");
                throw new OAuth2AuthenticationException("Email not found in OAuth2 user");
            }

            // 3. Create/update user
            User user = userRepo.findByEmailId(email)
                    .orElseGet(() -> {
                        log.info("Creating new user for email: {}", email);
                        User newUser = new User();
                        newUser.setEmailId(email);
                        newUser.setName(name);
                        newUser.setUsername(email);
                        newUser.setCreatedAt(java.time.Instant.now());
                        newUser.setProvider(User.AuthProvider.google);
                        newUser.setProviderId(providerId);
                        newUser.setUserType(User.UserType.BASE);
                        return userRepo.save(newUser);
                    });
            
            log.info("User processed successfully. User ID: {}", user.getUserId());
            
            // 4. Return the OAuth2User with the original attributes
            return new DefaultOidcUser(
                    oauthUser.getAuthorities(),
                    oauthUser.getIdToken(),
                    oauthUser.getUserInfo(),
                    "sub" // Name attribute key
            );
        } catch (Exception e) {
            log.error("Error in CustomOAuth2UserService", e);
            throw e;
        }
    }
}