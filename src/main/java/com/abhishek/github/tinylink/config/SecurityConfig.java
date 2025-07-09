package com.abhishek.github.tinylink.config;

import com.abhishek.github.tinylink.filter.OAuthValidationFilter;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.abhishek.github.tinylink.service.CustomOAuth2OidcUserService;
import com.abhishek.github.tinylink.util.OAuthTokenValidatorUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final CustomOAuth2OidcUserService customOAuth2OidcUserService;

    @Autowired
    SecurityConfig(UserRepository userRepository, ObjectMapper objectMapper,
                   CustomOAuth2OidcUserService customOAuth2OidcUserService) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.customOAuth2OidcUserService = customOAuth2OidcUserService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain...");
        
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", 
                    "/login**", 
                    "/error**",
                    "/oauth2/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
                .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
            .csrf(csrf -> csrf.disable())
            .oauth2Login(oauth -> {
                log.info("Configuring OAuth2 login...");
                oauth.userInfoEndpoint(userInfo -> {
                    userInfo.oidcUserService(customOAuth2OidcUserService);
                    log.info("Configuring user info endpoint...");
                });
                oauth.successHandler(new OAuth2AuthenticationSuccessHandler(userRepository, objectMapper));
            })
            .addFilterBefore(
                new OAuthValidationFilter(new OAuthTokenValidatorUtil()), 
                UsernamePasswordAuthenticationFilter.class
            );
            
        log.info("Security filter chain configuration complete.");
        return http.build();
    }
}
