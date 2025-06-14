package com.abhishek.github.tinylink.exception;

import lombok.Getter;

@Getter
public class TinyLinkException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;


    public TinyLinkException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
