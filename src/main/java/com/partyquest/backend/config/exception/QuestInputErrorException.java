package com.partyquest.backend.config.exception;

import lombok.Getter;

@Getter
public class QuestInputErrorException extends RuntimeException {
    private ErrorCode errorCode;

    public QuestInputErrorException(String msg,ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }
}
