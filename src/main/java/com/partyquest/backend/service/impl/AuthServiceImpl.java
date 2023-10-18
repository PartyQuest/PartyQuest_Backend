package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.redis.RedisDao;
import com.partyquest.backend.config.jwt.TokenProvider;
import com.partyquest.backend.config.exception.EmailDuplicationException;
import com.partyquest.backend.config.exception.EmailNotFoundException;
import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.repository.UserRepository;
import com.partyquest.backend.service.logic.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisDao redisDao;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository, TokenProvider tokenProvider, RedisDao redisDao) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.redisDao = redisDao;
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
        Optional<User> optionalUser = userRepository.findByEmailAndPassword(dto.getEmail(),dto.getPassword());
        if(optionalUser.isEmpty()) {
            throw new EmailNotFoundException("email not found", ErrorCode.EMAIL_NOT_FOUND);
        }
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
}
