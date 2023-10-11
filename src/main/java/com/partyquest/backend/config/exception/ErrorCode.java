package com.partyquest.backend.config.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND(404,"COMMON-ERR-404","PAGE NOT FOUND"),
    INTER_SERVER_ERROR(500,"COMMON-ERR-500","INTERNAL SERVER ERROR"),
    EMAIL_DUPLICATION(400,"MEMBER-ERR-400", "EMAIL DUPLICATED"),
    EMAIL_NOT_FOUND(400,"MEMBER-ERR-400", "MEMBER NOT FOUND"),
    ;

    private int status;
    private String errorCode;
    private String message;
}
