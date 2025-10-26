package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.model.AuthProviderEntity;
import com.abhishek.github.tinylink.model.CustomOidcUser;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.AuthProviderRepository;
import com.abhishek.github.tinylink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2OidcUserService extends OidcUserService {
    private final UserRepository userRepository;
    private final AuthProviderRepository authProviderRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        // Extract user info from OIDC claims
        Map<String, Object> attributes = oidcUser.getAttributes();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        String providerUserId = oidcUser.getName();

        boolean isEmailVerified = oidcUser.getEmailVerified();

        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase(Locale.ROOT);
        AuthProviderEntity.AuthProvider provider = AuthProviderEntity.AuthProvider.valueOf(registrationId);

        Optional<User> userOptional = authProviderRepository.findByProviderAndProviderUserId(
                provider, providerUserId
        ).map(AuthProviderEntity::getUser);

        User user = userOptional.orElseGet(() -> {
            User newUser = new User();

            newUser.setEmailId(email);
            newUser.setName(name);
            newUser.setUsername(email);
            newUser.setPassword("");
            newUser.setRegistrationCompleted(isEmailVerified);
            Set<User.UserRole> roles = new HashSet<>();
            roles.add(User.UserRole.ROLE_USER);
            newUser.setRoles(roles);
            newUser.setUserType(User.UserType.BASE);
            newUser.setCreatedAt(Instant.now());
            newUser.addAuthProvider(AuthProviderEntity.AuthProvider.GOOGLE, providerUserId);

            userRepository.save(newUser);
            return newUser;

        });

        return new CustomOidcUser(oidcUser, user);
    }
}