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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisDao redisDao;
    private final ProviderService providerService;

    @Value("${spring.social.bcrypt.key}")
    private String bcryptKey;

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
        BCryptService bCryptService = new BCryptService();
        //String nullCrypt = bCryptService.encodeBcrypt(null);

        Optional<User> alreadyUser = userRepository.findByEmail(dto.getEmail());
        if(alreadyUser.isPresent()) {
            throw new EmailDuplicationException("email duplicated", ErrorCode.EMAIL_DUPLICATION);
        }
        if(dto.getPassword() == null) {
            throw new PasswordException("NULL PASSWORD", ErrorCode.PASSWORD_ERROR);
        }
        User newUser = AuthDto.SignupDto.dtoToEntity(dto,type);

        return AuthDto.SignupResponseDto.entityToDto(userRepository.save(newUser));
    }

    @Override
    public AuthDto.LoginResponseDto Login(AuthDto.LoginRequestDto dto) {
        BCryptService bCryptService = new BCryptService();
        //String nullCrypt = bCryptService.encodeBcrypt(null);
        Optional<User> optUser = userRepository.findByEmail(dto.getEmail());
        if(optUser.isEmpty()) throw new EmailNotFoundException("EMAIL NOT FOUND", ErrorCode.EMAIL_NOT_FOUND);
        User user = optUser.get();
        if(dto.getPassword() == null) throw new PasswordException("NO LOCAL USER", ErrorCode.PASSWORD_ERROR);
        if(!bCryptService.matchesBcrypt(dto.getPassword(),user.getPassword())) throw new PasswordException("LOCAL LOGIN'S PASSWORD IS NOT NULL",ErrorCode.PASSWORD_ERROR);

        String[] tokens = tokenProvider.createToken(user).split("::");
        return AuthDto.LoginResponseDto.builder()
                .email(user.getEmail())
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .build();
//        if (dto.getPassword() == null) throw new PasswordException("LOCAL LOGIN'S PASSWORD IS NOT NULL",ErrorCode.PASSWORD_ERROR);
//        Optional<User> optionalUser = userRepository.findByEmailAndPassword(dto.getEmail(),dto.getPassword());
//        if(optionalUser.isEmpty()) throw new EmailNotFoundException("EMAIL NOT FOUND", ErrorCode.EMAIL_NOT_FOUND);
//        User user = optionalUser.get();
//        String[] tokens = tokenProvider.createToken(user).split("::");
//
//        return AuthDto.LoginResponseDto.builder()
//                .email(user.getEmail())
//                .accessToken(tokens[0])
//                .refreshToken(tokens[1])
//                .build();
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
    public AuthDto.LoginResponseDto OAuth2Login(AuthDto.OAuthLogin.Request dto, String provider) {
        BCryptService bCryptService = new BCryptService();
        if(!bCryptService.matchesBcrypt(bcryptKey,dto.getSecrets())) throw new PasswordException("LOCAL LOGIN'S PASSWORD IS NOT NULL",ErrorCode.PASSWORD_ERROR);
        Optional<User> optUser = userRepository.findByEmail(dto.getEmail());

        User user;
        if(optUser.isEmpty()) {
            user = User.builder()
                    .sns(provider)
                    .email(dto.getEmail())
                    .nickname(dto.getNickname())
                    .password(null)
                    .build();
            user = userRepository.save(user);
        } else {
            user = optUser.get();
        }

        String[] tokens = tokenProvider.createToken(user).split("::");

        return AuthDto.LoginResponseDto.builder()
                .email(user.getEmail())
                .accessToken(tokens[0])
                .refreshToken(tokens[1])
                .build();
    }
}
