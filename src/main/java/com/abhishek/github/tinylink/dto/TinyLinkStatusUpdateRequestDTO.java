package com.abhishek.github.tinylink.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Request body payload for deactivating or updating a tiny link's status")
public class TinyLinkStatusUpdateRequestDTO {
   @NotBlank(message = "Tiny code cannot be empty")
   @Schema(description = "The short code of the tiny link to be deactivated", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcdef")
   private String tinyCode;

}
