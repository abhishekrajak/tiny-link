package com.abhishek.github.tinylink.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContextService {
    String getUserIdFromToken() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new Exception("User is not authenticated");
        }

        return authentication.getName();
    }
}
