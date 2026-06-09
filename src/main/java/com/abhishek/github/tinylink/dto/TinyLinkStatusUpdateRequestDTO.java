package com.abhishek.github.tinylink.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TinyLinkStatusUpdateRequestDTO {
   @NotBlank(message = "Tiny code cannot be empty")
   private String tinyCode;

}
