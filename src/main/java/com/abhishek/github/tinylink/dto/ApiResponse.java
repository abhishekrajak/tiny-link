package com.abhishek.github.tinylink.dto;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@AllArgsConstructor
@Schema(description = "Standard API response wrapper containing the response data or error details")
public class ApiResponse<T> {
    @Schema(description = "The error code, 'SUCCESS' if the operation succeeded", example = "SUCCESS")
    private String errorCode;

    @Schema(description = "Detailed error message or status description", example = "Success")
    private String errorMessage;

    @Schema(description = "The payload of the response, type varies depending on the endpoint")
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ApiErrorCodes.successCode, ApiErrorMessages.successMessage, data);
    }

}
