package com.abhishek.github.tinylink.dto;

import com.abhishek.github.tinylink.model.LinkStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TinyLinkStatusUpdateRequestDTO {
   @NotBlank(message = "Tiny code cannot be empty")
   private String tinyCode;

}
