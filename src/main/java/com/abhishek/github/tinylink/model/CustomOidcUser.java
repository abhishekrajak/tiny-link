package com.abhishek.github.tinylink.model;

import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomOidcUser extends DefaultOidcUser {
    private final User user;

    public CustomOidcUser(OidcUser oidcUser, User user) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken(), "sub");
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
