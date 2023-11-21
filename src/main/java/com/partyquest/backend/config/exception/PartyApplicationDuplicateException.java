package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class PartyApplicationDuplicateException extends RuntimeException{
    private ErrorCode errorCode;

    public PartyApplicationDuplicateException(ErrorCode errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }
}
