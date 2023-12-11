package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.exception.PasswordException;
import com.partyquest.backend.config.redis.RedisDao;
import com.partyquest.backend.config.jwt.TokenProvider;
import com.partyquest.backend.config.exception.EmailDuplicationException;
import com.partyquest.backend.config.exception.EmailNotFoundException;
import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.domain.dto.AuthDto;
import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.repository.FileRepository;
import com.partyquest.backend.domain.repository.UserRepository;
import com.partyquest.backend.domain.type.FileType;
import com.partyquest.backend.service.impl.oauth2.AccessToken;
import com.partyquest.backend.service.impl.oauth2.ProviderService;
import com.partyquest.backend.service.impl.oauth2.profile.ProfileDto;
import com.partyquest.backend.service.logic.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisDao redisDao;
    private final ProviderService providerService;
    private final FileRepository fileRepository;

    @Value("${spring.social.bcrypt.key}")
    private String bcryptKey;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           TokenProvider tokenProvider,
                           RedisDao redisDao,
                           ProviderService providerService,
                           FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.redisDao = redisDao;
        this.providerService = providerService;
        this.fileRepository = fileRepository;
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
        newUser = userRepository.save(newUser);

        //TODO: 임시로 넣은 파일 메타데이터, 실제 파일서비스 구현할 때 수정해야함!!!!!!!!!
        File file = File.builder()
                .fileSize(1234L)
                .type(FileType.USER_THUMBNAIL)
                .fileOriginalName("testOriginName")
                .filePath("testFilePath")
                .fileAttachChngName("testAttachChngName")
                .user(newUser)
                .build();
        file = fileRepository.save(file);
        newUser.getFiles().add(file);
        return AuthDto.SignupResponseDto.entityToDto(newUser);
    }

    @Override
    public AuthDto.LoginResponseDto Login(AuthDto.LoginRequestDto dto) {
        BCryptService bCryptService = new BCryptService();
        Optional<User> optUser = userRepository.findByEmail(dto.getEmail());
        if(optUser.isEmpty()) throw new EmailNotFoundException("EMAIL NOT FOUND", ErrorCode.EMAIL_NOT_FOUND);
        User user = optUser.get();
        if(dto.getPassword() == null) throw new PasswordException("NO LOCAL USER", ErrorCode.PASSWORD_ERROR);
        if(!bCryptService.matchesBcrypt(dto.getPassword(),user.getPassword())) throw new PasswordException("LOCAL LOGIN'S PASSWORD IS NOT NULL",ErrorCode.PASSWORD_ERROR);

        String[] tokens = tokenProvider.createToken(user).split("::");
        return TokenDtoBuilder(tokens,user.getEmail());
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

        return TokenDtoBuilder(tokens,user.getEmail());
    }

    @Override
    public AuthDto.LoginResponseDto RefreshToken(HttpServletRequest request, HttpServletResponse response) {
        Long id = tokenProvider.getUserId(request, response);
        Optional<User> optUser = userRepository.findById(id);
        if(optUser.isEmpty()) throw new EmailNotFoundException("NOT FOUND USER",ErrorCode.EMAIL_NOT_FOUND);
        User user = optUser.get();
        String[] tokens = tokenProvider.createToken(user).split("::");

        return TokenDtoBuilder(tokens,user.getEmail());
    }

    private AuthDto.LoginResponseDto TokenDtoBuilder(String[] tokens, String email) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessExpired = now.plusHours(1);
        LocalDateTime refreshExpired = now.plusMonths(1);

        return AuthDto.LoginResponseDto.builder()
                .accessExpiredAt(accessExpired.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .refreshExpiredAt(refreshExpired.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .email(email)
                .refreshToken(tokens[1])
                .accessToken(tokens[0])
                .build();
    }

    @Override
    public AuthDto.UserSpecificationDto.Response getUserSpecificationByID(long userID) {
        return
                userRepository.findById(userID)
                        .map(user -> AuthDto.UserSpecificationDto.Response.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .nickname(user.getNickname())
                                .SNS(user.getSns())
                                .build())
                        .orElseThrow(() -> new EmailNotFoundException("NOT FOUND USER",ErrorCode.PARTY_NOT_FOUND));
    }

    @Override
    public void ChangeUserSpecification(long userID, AuthDto.UserSpecificationDto.Request dto) {
        User user = userRepository.findById(userID).orElseThrow(() -> new EmailNotFoundException("NOT FOUND USER",ErrorCode.PARTY_NOT_FOUND));
        user.setNickname(dto.getNickname());
    }
}
