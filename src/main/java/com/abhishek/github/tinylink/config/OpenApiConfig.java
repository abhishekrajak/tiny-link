package com.abhishek.github.tinylink.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Abhishek Rajak",
                        email = "abhishekrajak100@gmail.com",
                        url = "https://www.github.com/abhishekrajak"
                ),
                description = "Open API Specification for Tiny Link API",
                title = "Open API Specification - Tiny Link API",
                version = "1.0"
        )
)
@SecurityScheme(
        name = "BearerAuth",
        description = "JWT Auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
