package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class NotPartyMemberException extends RuntimeException {
    private ErrorCode errorCode;

    public NotPartyMemberException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
