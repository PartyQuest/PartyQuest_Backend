package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.exception.PasswordException;
import com.partyquest.backend.config.redis.RedisDao;
import com.partyquest.backend.config.jwt.TokenProvider;
import com.partyquest.backend.config.exception.EmailDuplicationException;
import com.partyquest.backend.config.exception.EmailNotFoundException;
import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.repository.UserRepository;
import com.partyquest.backend.service.impl.oauth2.AccessToken;
import com.partyquest.backend.service.impl.oauth2.ProviderService;
import com.partyquest.backend.service.impl.oauth2.profile.ProfileDto;
import com.partyquest.backend.service.logic.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisDao redisDao;
    private final ProviderService providerService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           TokenProvider tokenProvider,
                           RedisDao redisDao,
                           ProviderService providerService) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.redisDao = redisDao;
        this.providerService = providerService;
    }

    @Override
    public AuthDto.SignupResponseDto SignUp(AuthDto.SignupDto dto, String type) {
        Optional<User> alreadyUser = userRepository.findByEmail(dto.getEmail());
        if(alreadyUser.isPresent()) {
            throw new EmailDuplicationException("email duplicated", ErrorCode.EMAIL_DUPLICATION);
        }
        User newUser = AuthDto.SignupDto.dtoToEntity(dto,type);

        return AuthDto.SignupResponseDto.entityToDto(userRepository.save(newUser));
    }

    @Override
    public AuthDto.LoginResponseDto Login(AuthDto.LoginRequestDto dto) {
        if (dto.getPassword() == null) throw new PasswordException("LOCAL LOGIN'S PASSWORD IS NOT NULL",ErrorCode.PASSWORD_ERROR);
        Optional<User> optionalUser = userRepository.findByEmailAndPassword(dto.getEmail(),dto.getPassword());
        if(optionalUser.isEmpty()) throw new EmailNotFoundException("EMAIL NOT FOUND", ErrorCode.EMAIL_NOT_FOUND);
        User user = optionalUser.get();
        String[] tokens = tokenProvider.createToken(user).split("::");

        return AuthDto.LoginResponseDto.builder()
                .email(user.getEmail())
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .build();
    }

    @Override
    public void Logout(long id) {
        redisDao.deleteValue(Long.toString(id));
    }

    @Override
    public String getEmailById(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            return optionalUser.get().getEmail();
        } else {
            throw new EmailNotFoundException("email not found",ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new EmailNotFoundException("email not found",ErrorCode.EMAIL_NOT_FOUND);
        }
    }

    @Override
    public AuthDto.LoginResponseDto OAuth2Login(String code, String provider) {
        AccessToken accessToken = providerService.getAccessToken(code, provider);
        ProfileDto profile = providerService.getProfile(accessToken.getAccess_token(),provider);

        Optional<User> optionalUser = userRepository.findByEmail(profile.getEmail());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            String[] tokens = tokenProvider.createToken(user).split("::");
            return AuthDto.LoginResponseDto.builder()
                    .email(user.getEmail())
                    .accessToken(tokens[0])
                    .refreshToken(tokens[1])
                    .build();
        } else {
            User user = User.builder()
                    .sns(provider)
                    .email(profile.getEmail())
                    .birth(null)
                    .nickname(profile.getNickname())
                    .password(null)
                    .build();
            user = userRepository.save(user);

            String[] tokens = tokenProvider.createToken(user).split("::");
            return AuthDto.LoginResponseDto.builder()
                    .email(user.getEmail())
                    .accessToken(tokens[0])
                    .refreshToken(tokens[1])
                    .build();
        }
    }
}
