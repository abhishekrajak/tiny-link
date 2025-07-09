package com.abhishek.github.tinylink.config;

import com.abhishek.github.tinylink.model.User;
import com.abhishek.github.tinylink.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public OAuth2AuthenticationSuccessHandler(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 authentication successful");
        
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            log.info("OAuth2User principal: {}", oAuth2User);
            
            Map<String, Object> attributes = oAuth2User.getAttributes();
            log.info("User attributes: {}", attributes);

            // Extract user info from OAuth2 response
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String providerId = (String) attributes.get("sub");
            
            if (email == null) {
                log.error("Email not found in OAuth2 user attributes");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email not found in OAuth2 user");
                return;
            }

            log.info("Processing OAuth2 login for user: {}", email);
            
            // Process user login
//            User user = processOAuthPostLogin(email, name);

            // Create response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("email", email);
            responseData.put("name", name);
            responseData.put("providerId", providerId);
//            responseData.put("id", user.getProviderId());
            responseData.put("success", true);
            
            log.info("Sending success response for user: {}", email);
            
            // Set response type and write JSON
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(responseData));
            response.getWriter().flush();
            
        } catch (Exception e) {
            log.error("Error in OAuth2 authentication success handler", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"Authentication processing failed\"}");
            response.getWriter().flush();
        }
    }

    private User processOAuthPostLogin(String email, String name) {
//        Optional<User> userOptional = userRepository.findByEmailId(email);
//
//        if (userOptional.isEmpty()) {
//            // Create new user
//            User newUser = new User();
//            newUser.setEmailId(email);
//            newUser.setName(name != null ? name : "");
//            // Set other default values as needed
//            return userRepository.save(newUser);
//        }
//
//        return userOptional.get();

        return null;
    }
}