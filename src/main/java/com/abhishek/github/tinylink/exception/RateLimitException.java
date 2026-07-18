package com.abhishek.github.tinylink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitException extends RuntimeException {

    String message;

    public RateLimitException(String message) {
        this.message = message;
    }
}
