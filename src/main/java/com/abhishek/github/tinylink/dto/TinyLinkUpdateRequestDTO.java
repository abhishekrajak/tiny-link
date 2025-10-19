package com.abhishek.github.tinylink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TinyLinkUpdateRequestDTO {
   @NotBlank(message = "Tiny code cannot be empty")
   private String tinyCode;
   @NotBlank(message = "Redirection URL cannot be empty")
   private String redirectionLink;

}
