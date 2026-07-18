package com.abhishek.github.tinylink.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {

    com.abhishek.github.tinylink.model.User.UserType userType;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                      com.abhishek.github.tinylink.model.User.UserType userType) {
        super(username, password, authorities);
        this.userType = userType;
    }


}
