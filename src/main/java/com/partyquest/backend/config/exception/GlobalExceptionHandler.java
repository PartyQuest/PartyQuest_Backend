package com.partyquest.backend.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailDuplicationException.class)
    public ResponseEntity<ErrorResponse> handleEmailDuplicationException(EmailDuplicationException ex) {
        log.error("handleEmailDuplicationException", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotFoundException(EmailNotFoundException ex) {
        log.error("handleEmailNotFoundException", ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(OAuth2Exception.class)
    public ResponseEntity<ErrorResponse> handleOAuth2Exception(OAuth2Exception ex) {
        log.error("handleOAuth2Exception",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<ErrorResponse> handlePasswordException(PasswordException ex) {
        log.error("handlePasswordException",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(EnumTypeNotMatchedException.class)
    public ResponseEntity<ErrorResponse> handleEnumTypeNotMatchedException(EnumTypeNotMatchedException ex) {
        log.error("handleEnumTypeNotMatchedException",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex){
        log.error("handleException",ex);
        ErrorResponse response = new ErrorResponse(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PartyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePartyNotFoundException(PartyNotFoundException ex) {
        log.error("handlePartyNotFoundException",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(PartyApplicationDuplicateException.class)
    public ResponseEntity<ErrorResponse> handlePartyApplicationDuplicationException(PartyApplicationDuplicateException ex) {
        log.error("handlePartyApplicationDuplication",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(NotAdminException.class)
    public ResponseEntity<ErrorResponse> handleNotAdminException(NotAdminException ex) {
        log.error("handleNotAdminException",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(NotPartyMemberException.class)
    public ResponseEntity<ErrorResponse> handleNotPartyMemberException(NotPartyMemberException ex) {
        log.error("handleNotPartyMemberException",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(PartyMemberException.class)
    public ResponseEntity<ErrorResponse> handlePartyMemberException(PartyMemberException ex) {
        log.error("handlePartyMemberException",ex);
        ErrorResponse response = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(response,HttpStatus.valueOf(ex.getErrorCode().getStatus()));
    }
}
