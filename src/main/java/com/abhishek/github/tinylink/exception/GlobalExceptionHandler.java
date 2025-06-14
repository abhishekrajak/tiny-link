package com.abhishek.github.tinylink.exception;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TinyLinkException.class)
    public ApiResponse<?> handleValidationExceptions(
            TinyLinkException exception) {
        return new ApiResponse<>(exception.getErrorCode(), exception.getErrorMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleAllExceptions() {
        return new ApiResponse<>(ApiErrorCodes.unknownErrorCode, ApiErrorMessages.unknownErrorMessage, null);
    }
}