package com.abhishek.github.tinylink.service;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.exception.TinyLinkException;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    UserRepository userRepository;

    @Autowired
    public RoleService(UserRepository userRepo) {
        this.userRepository = userRepo;
    }

    public void assignRole(String email, User.UserRole role) {
        User user = userRepository.findByEmailId(email)
                .orElseThrow(() -> new TinyLinkException(ApiErrorCodes.userNotFound, "User not found."));
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
