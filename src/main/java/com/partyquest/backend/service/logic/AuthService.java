package com.partyquest.backend.service.logic;

import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public interface AuthService {
    AuthDto.SignupResponseDto Signup(AuthDto.SignupDto dto, String type);
    AuthDto.LoginResponseDto Login(AuthDto.LoginRequestDto dto);
    void Logout(long id);
    String getEmailById(long id);
}