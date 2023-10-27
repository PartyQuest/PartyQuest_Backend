package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class PasswordException extends RuntimeException{
    private ErrorCode errorCode;

    public PasswordException(String msg, ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }
}
