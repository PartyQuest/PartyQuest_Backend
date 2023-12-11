package com.partyquest.backend.service.logic;

import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

public interface AuthService {
    AuthDto.SignupResponseDto SignUp(AuthDto.SignupDto dto, String type);
    AuthDto.LoginResponseDto Login(AuthDto.LoginRequestDto dto);
    void Logout(long id);
    String getEmailById(long id);
    User getUserByEmail(String email);
    AuthDto.LoginResponseDto OAuth2Login(AuthDto.OAuthLogin.Request dto,String provider);
    AuthDto.LoginResponseDto RefreshToken(HttpServletRequest request, HttpServletResponse response);
    AuthDto.UserSpecificationDto.Response getUserSpecificationByID(long userID);
    void ChangeUserSpecification(long userID ,AuthDto.UserSpecificationDto.Request dto);

}
