package com.abhishek.github.tinylink.config;

import com.abhishek.github.tinylink.model.CustomOidcUser;
import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    @Value("${app.config.redirect-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        CustomOidcUser user = (CustomOidcUser) authentication.getPrincipal();

        try {

            String userId = user.getUserId();
            String email = user.getEmail();
            String name = user.getName();
            User.UserType userType = user.getUserType();

            List<String> roles = user.getRoles();

            String token = jwtTokenUtil.generateToken(userId, email, name, roles, userType);

            String redirectUrl = String.format("%s?token=%s", frontendUrl, token);

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (Exception e) {
            response.sendError(HttpStatus.OK.value(), "Auth done but token gen failed");
        }
    }
}