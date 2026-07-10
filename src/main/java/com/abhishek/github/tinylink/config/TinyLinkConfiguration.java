package com.abhishek.github.tinylink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.config")
@Getter
@Setter
public class TinyLinkConfiguration {

     private int tinyUrlCodeLength;

     private String tinyLinkAllowedChars;

     private int shortCodeGenerationMaxRetryCount;

     private int baseUserMaxLinks;

     private int specialUserMaxLinks;

     private int corporateUserMaxLinks;

     private String apiBaseUrl;
}
