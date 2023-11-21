package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class PartyNotFoundException extends RuntimeException {
    private ErrorCode errorCode;
    public PartyNotFoundException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
