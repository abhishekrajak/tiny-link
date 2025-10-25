package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User processOAuthPostLogin(GoogleIdToken.Payload payload) {

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
            Set<User.UserRole> roles = new HashSet<>();
            roles.add(User.UserRole.ROLE_USER);
            newUser.setRoles(roles);
            newUser.setUserType(User.UserType.BASE);
            newUser.setCreatedAt(Instant.now());
            user = userRepository.save(newUser);
        } else {
            user = userOptional.get();
        }

        return user;
    }

    public User completeRegistration(String email) {
        return null;
    }

}
