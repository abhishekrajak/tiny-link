package com.abhishek.github.tinylink.dto;

import com.abhishek.github.tinylink.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TinyLinkGenerateRequestDTO {
   private String tinyCode;
   private String redirectionLink;
   private User user;

}
