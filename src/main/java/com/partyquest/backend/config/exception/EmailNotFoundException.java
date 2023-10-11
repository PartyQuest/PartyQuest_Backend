package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class EmailNotFoundException extends RuntimeException{
    private ErrorCode errorCode;

    public EmailNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
