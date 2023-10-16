package com.partyquest.backend.controller;

import com.partyquest.backend.config.ResponseEntityFactory;
import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.service.logic.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> Signup(@RequestBody AuthDto.SignupDto dto) {
        log.info(dto.getEmail());
//        AuthDto.SignupResponseDto result = authService.SignUp(dto, "LOCAL");
        String local = "LOCAL";
        AuthDto.SignupResponseDto result = authService.SignUp(dto, local);
        log.info(dto.getEmail());
        return ResponseEntityFactory.createResponse("/user/{id}",result.getId(),result);
    }
}
