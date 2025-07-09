package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User processOAuthPostLogin(String email, String name) {

        return null;
    }

    public User completeRegistration(String email) {
        return null;
    }

}
