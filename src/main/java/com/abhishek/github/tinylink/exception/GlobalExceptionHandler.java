package com.abhishek.github.tinylink.exception;

import com.abhishek.github.tinylink.constant.ApiErrorCodes;
import com.abhishek.github.tinylink.constant.ApiErrorMessages;
import com.abhishek.github.tinylink.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.http.HttpResponse;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TinyLinkException.class)
    public ApiResponse<?> handleValidationExceptions(
            TinyLinkException exception) {
        log.error("{} {}", exception.getErrorCode(), exception.getErrorMessage());
        return new ApiResponse<>(exception.getErrorCode(), exception.getErrorMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleAllExceptions(Exception ex) {
        log.error("Unexpected error : ", ex);
        return new ApiResponse<>(ApiErrorCodes.unknownErrorCode, ApiErrorMessages.unknownErrorMessage, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Validation failed");

        return new ApiResponse<>("VALIDATION_ERROR", errorMessage, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException ex) {
        return new ApiResponse<>("ACCESS_DENIED", ex.getMessage(), null);
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<Void> handleRateLimitException(RateLimitException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }
}