package com.abhishek.github.tinylink.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Response payload representing User Details")
public class TinyLinkUserDTO {
    @Schema(description = "User Email ID")
    private String emailId;

    @Schema(description = "User JWT Token")
    private String jwtToken;
}
