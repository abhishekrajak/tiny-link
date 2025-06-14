package com.abhishek.github.tinylink.dto;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private String errorCode;
    private String errorMessage;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ApiErrorCodes.successCode, ApiErrorMessages.successMessage, data);
    }

}
