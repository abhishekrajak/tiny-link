package com.abhishek.github.tinylink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object containing click/visit analytics event details for a tiny link")
public class TinyLinkAnalyticsEventDTO {
   @NotBlank(message = "Tiny code cannot be empty")
   @Schema(description = "The short code of the tiny link visited", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcdef")
   private String tinyCode;

   @Schema(description = "IP address of the visitor", example = "192.168.1.1")
   private String ipAddress;

   @Schema(description = "User-Agent header string of the visitor's browser/client", example = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)")
   private String userAgent;

   @Schema(description = "Referer header string indicating from where the visitor navigated", example = "https://t.co/")
   private String referer;
}
