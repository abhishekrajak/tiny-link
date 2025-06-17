package com.abhishek.github.tinylink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.abhishek.github.tinylink.constant.StringConstants.ConfigConstant.API_URL;

@Configuration
@ConfigurationProperties(prefix = API_URL)
@Getter
@Setter
public class TinyLinkConfiguration {

     private int tinyUrlCodeLength;

     private String tinyLinkAllowedChars;

     private int shortCodeGenerationMaxRetryCount;
}
