package com.abhishek.github.tinylink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.abhishek.github.tinylink.constant.StringConstants.ConfigConstant.API_URL;

@Configuration
@ConfigurationProperties(prefix = "perf")
@Getter
@Setter
@Profile("performance")
public class ProfileModeConfiguration {

     private int tinyUrlCodeLength;

     private String tinyLinkAllowedChars;

     private int shortCodeGenerationMaxRetryCount;
}
