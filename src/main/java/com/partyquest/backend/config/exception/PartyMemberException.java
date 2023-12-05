package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class PartyMemberException extends RuntimeException {
    private ErrorCode errorCode;
    public PartyMemberException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
