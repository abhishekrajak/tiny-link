package com.abhishek.github.tinylink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Request body payload for updating a tiny link's destination URL")
public class TinyLinkUpdateRequestDTO {
   @NotBlank(message = "Tiny code cannot be empty")
   @Schema(description = "The short code of the existing tiny link that needs to be updated", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcdef")
   private String tinyCode;

   @NotBlank(message = "Redirection URL cannot be empty")
   @Schema(description = "The new destination URL where the tiny link should redirect to", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.github.com")
   @NotBlank(message = "Redirection link is required")
   private String redirectionLink;

}
