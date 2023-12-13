package com.partyquest.backend.controller;

import com.partyquest.backend.config.ResponseEntityFactory;
import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.service.impl.oauth2.AccessToken;
import com.partyquest.backend.service.impl.oauth2.ProviderService;
import com.partyquest.backend.service.impl.oauth2.profile.ProfileDto;
import com.partyquest.backend.service.logic.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final ProviderService providerService;

    @Autowired
    public AuthController(AuthService authService, ProviderService providerService) {
        this.authService = authService;
        this.providerService = providerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> Signup(@RequestBody AuthDto.SignupDto dto) {
        log.info(dto.getEmail());
        String local = "LOCAL";
        AuthDto.SignupResponseDto result = authService.SignUp(dto, local);
        log.info(dto.getEmail());
        return ResponseEntityFactory.createResponse("/user/{id}", result.getId(), result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody AuthDto.LoginRequestDto dto) {
        AuthDto.LoginResponseDto responseDto = authService.Login(dto);
        return ResponseEntityFactory.okResponse(responseDto);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<?> Logout(@AuthenticationPrincipal long id) {
        authService.Logout(id);
        return ResponseEntityFactory.noResponse();
    }

    @PostMapping("/login/oauth/{provider}")
    public ResponseEntity<?> OAuthLogin(@RequestBody AuthDto.OAuthLogin.Request dto, @PathVariable String provider) {
        AuthDto.LoginResponseDto result = authService.OAuth2Login(dto, provider);
        return ResponseEntityFactory.okResponse(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> RefreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntityFactory.okResponse(authService.RefreshToken(request, response));
    }

    @GetMapping("/member/my-account")
    public ResponseEntity<?> GetMyAccountData(@AuthenticationPrincipal long id) {
        return ResponseEntityFactory.okResponse(authService.getUserSpecificationByID(id));
    }

    @PatchMapping("/member")
    public ResponseEntity<?> ModifyMyAccountData(@AuthenticationPrincipal long id,
                                                 @RequestBody AuthDto.UserSpecificationDto.Request dto) {
        authService.ChangeUserSpecification(id, dto);
        return ResponseEntityFactory.noResponse();
    }

    @DeleteMapping("/member")
    public ResponseEntity<?> DeleteAccountData(@AuthenticationPrincipal long id) {
        authService.DeleteAccountData(id);
        return ResponseEntityFactory.noResponse();
    }
}