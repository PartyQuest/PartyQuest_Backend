package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class EnumTypeNotMatchedException extends RuntimeException{
    private ErrorCode errorCode;
    public EnumTypeNotMatchedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
