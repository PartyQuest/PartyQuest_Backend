package com.partyquest.backend.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.domain.dto.ResponseWrapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException ex) {
            String message = ex.getMessage();
            if(ErrorCode.JWT_TOKEN_EXPIRED.getMessage().equals(message)) {
                setResponse(response,ErrorCode.JWT_TOKEN_EXPIRED);
            } else if(ErrorCode.JWT_TOKEN_MALFORMED.getMessage().equals(message)) {
                setResponse(response,ErrorCode.JWT_TOKEN_MALFORMED);
            } else if(ErrorCode.JWT_TOKEN_WRONG_TYPE.getMessage().equals(message)) {
                setResponse(response,ErrorCode.JWT_TOKEN_WRONG_TYPE);
            } else {
                setResponse(response,ErrorCode.ACCESS_DENIED);
            }
        }
    }
    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws RuntimeException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus());

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("status",String.valueOf(errorCode.getStatus()));
        map.put("error_code", errorCode.getErrorCode());
        map.put("message", errorCode.getMessage());
        response.getWriter().print(objectMapper.writeValueAsString(ResponseWrapper.addObject(map, HttpStatus.UNAUTHORIZED)));
    }
}
