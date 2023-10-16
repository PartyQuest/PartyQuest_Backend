package com.partyquest.backend.service.impl;

import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AuthServiceImplTest {

    private final AuthServiceImpl authService;
    private final UserRepository userRepository;

    @Autowired
    AuthServiceImplTest(AuthServiceImpl authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }


    @Test
    void SignUpTest() {
        AuthDto.SignupDto dto = AuthDto.SignupDto.builder()
                .email("email")
                .birth("birth")
                .nickname("nickname")
                .password("password")
                .build();
        AuthDto.SignupResponseDto signup = authService.SignUp(dto, "LOCAL");
        assertAll(
                () -> assertEquals(signup.getEmail(), "email")
        );
    }
    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    void LoginTest() {

        AuthDto.SignupDto dtos = AuthDto.SignupDto.builder()
                .email("emails")
                .birth("birth")
                .nickname("nickname")
                .password("password")
                .build();
        AuthDto.SignupResponseDto signup = authService.SignUp(dtos, "LOCAL");


        AuthDto.LoginRequestDto dto = AuthDto.LoginRequestDto.builder()
                .email("emails")
                .password("password")
                .build();
        AuthDto.LoginResponseDto login = authService.Login(dto);
        System.out.println(login.toString());
    }

    @Test
    void LogoutTest() {
        authService.Logout(0);
    }
}