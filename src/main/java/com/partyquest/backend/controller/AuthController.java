package com.partyquest.backend.controller;

import com.partyquest.backend.config.ResponseEntityFactory;
import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.service.impl.oauth2.AccessToken;
import com.partyquest.backend.service.impl.oauth2.ProviderService;
import com.partyquest.backend.service.impl.oauth2.profile.ProfileDto;
import com.partyquest.backend.service.logic.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final ProviderService providerService;

    @Autowired
    public AuthController(AuthService authService,ProviderService providerService) {
        this.authService = authService;
        this.providerService = providerService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> Signup(@RequestBody AuthDto.SignupDto dto) {
        log.info(dto.getEmail());
        String local = "LOCAL";
        AuthDto.SignupResponseDto result = authService.SignUp(dto, local);
        log.info(dto.getEmail());
        return ResponseEntityFactory.createResponse("/user/{id}",result.getId(),result);
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

    @GetMapping("/oauth/{provider}/callback")
    public ResponseEntity<?> OAuth2Login(@PathVariable("provider") String provider, @RequestParam("code") String code) {
        AuthDto.LoginResponseDto dto = authService.OAuth2Login(code, provider);
        return ResponseEntityFactory.okResponse(dto);
    }
}