package com.partyquest.backend.service.impl;

import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.service.logic.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthServiceImplTest {

    private final AuthServiceImpl authService;

    @Autowired
    AuthServiceImplTest(AuthServiceImpl authService) {
        this.authService = authService;
    }


    @Test
    void SignUpTest() {
        AuthDto.SignupDto dto = AuthDto.SignupDto.builder()
                .email("email")
                .birth("birth")
                .nickname("nickname")
                .password("password")
                .build();
        AuthDto.SignupResponseDto signup = authService.Signup(dto, "LOCAL");
        assertAll(
                () -> assertEquals(signup.getId(), 1),
                () -> assertEquals(signup.getEmail(), "email")
        );
    }

    @Test
    void LoginTest() {
        SignUpTest();
        AuthDto.LoginRequestDto dto = AuthDto.LoginRequestDto.builder()
                .email("email2")
                .password("password2")
                .build();
        AuthDto.LoginResponseDto login = authService.Login(dto);
        System.out.println(login.toString());
    }

    @Test
    void LogoutTest() {
        LoginTest();
        authService.Logout(0);
    }
}