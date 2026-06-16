package com.abhishek.github.tinylink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TinyLinkAnalyticsEventDTO {
   @NotBlank(message = "Tiny code cannot be empty")
   private String tinyCode;

   private String ipAddress;

   private String userAgent;

   private String referer;
}
