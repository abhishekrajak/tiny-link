package com.abhishek.github.tinylink.config;

import com.abhishek.github.tinylink.filter.JwtAuthenticationFilter;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.abhishek.github.tinylink.service.CustomOAuth2OidcUserService;
import com.abhishek.github.tinylink.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2OidcUserService customOAuth2OidcUserService;
    private final JwtTokenUtil jwtTokenUtil;

    @Value("${app.url.base-url}")
    private String frontEndBaseUrl;

    @Autowired
    SecurityConfig(UserRepository userRepository, ObjectMapper objectMapper,
                   CustomOAuth2OidcUserService customOAuth2OidcUserService,
                   JwtTokenUtil jwtTokenUtil) {
        this.customOAuth2OidcUserService = customOAuth2OidcUserService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(frontEndBaseUrl) // Flutter web origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain...");

        http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(
                                customOAuth2OidcUserService)
                        )
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureHandler((request, response, exception) -> {
                            response.sendError(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(
                        jwtAuthenticationFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
        ;

        log.info("Security filter chain configuration complete.");
        return http.build();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(jwtTokenUtil);
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil);
    }

}
