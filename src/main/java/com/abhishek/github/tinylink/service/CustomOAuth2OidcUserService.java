package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.model.CustomOidcUser;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2OidcUserService extends OidcUserService {
    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        // Extract user info from OIDC claims
        Map<String, Object> attributes = oidcUser.getAttributes();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        String providerId = oidcUser.getSubject();

        boolean isEmailVerified = oidcUser.getEmailVerified();

        // Find or create user
        User user = userRepository.findByEmailId(email)
                .orElseGet(() -> {

                    // TODO add some code for new user
                    User newUser = new User();

                    newUser.setEmailId(email);
                    newUser.setName(name);
                    newUser.setUsername(email);
                    newUser.setProvider(User.AuthProvider.google);
                    newUser.setProviderId(providerId);
                    newUser.setPassword("");
                    newUser.setRegistrationCompleted(isEmailVerified);
                    Set<User.UserRole> roles = new HashSet<>();
                    roles.add(User.UserRole.ROLE_USER);
                    newUser.setRoles(roles);
                    newUser.setUserType(User.UserType.BASE);
                    newUser.setCreatedAt(Instant.now());
                    userRepository.save(newUser);
                    return newUser;
                });

        return new CustomOidcUser(oidcUser, user);
    }
}