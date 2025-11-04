package com.abhishek.github.tinylink.model;

import lombok.Getter;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;

@Getter
public class CustomOidcUser extends DefaultOidcUser {
    private final String userId;
    private final String email;
    private final String name;
    private final List<String> roles;


    public CustomOidcUser(OidcUser oidcUser, User user) {
        super(oidcUser.getAuthorities(), oidcUser.getIdToken(), "sub");
        this.userId = user.getUserId().toString();
        this.email = user.getEmailId();
        this.name = user.getName();
        this.roles = user.getRoles().stream().map(Enum::name).toList();
    }

}
