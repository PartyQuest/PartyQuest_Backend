package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class EmailDuplicationException extends RuntimeException{
    private ErrorCode errorCode;
    public EmailDuplicationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
