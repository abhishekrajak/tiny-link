package com.abhishek.github.tinylink.dto;

import com.abhishek.github.tinylink.validation.ValidTinyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Request body payload for generating/creating a new tiny link")
public class TinyLinkGenerateRequestDTO {
   @Schema(description = "Optional custom short code. If not provided, a random code will be generated.", example = "ABHI1331")
   @ValidTinyCode
   private String tinyCode;

   @Schema(description = "The target destination URL where the tiny link should redirect to", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.google.com")
   @NotBlank(message = "Redirection link is required")
   private String redirectionLink;

}
