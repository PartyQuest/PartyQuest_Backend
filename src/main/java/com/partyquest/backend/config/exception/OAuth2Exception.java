package com.partyquest.backend.config.exception;


import lombok.Getter;

@Getter
public class OAuth2Exception extends RuntimeException {
    private ErrorCode errorCode;

    public OAuth2Exception(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
