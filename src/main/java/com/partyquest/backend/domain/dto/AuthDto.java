package com.partyquest.backend.domain.dto;


import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.service.impl.BCryptService;
import lombok.*;

import java.util.LinkedList;
@Data
@Builder
@Getter
@Setter
public class AuthDto {
    @Data
    @Builder
    public static class OAuthLogin {

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Request {
            private String email;
            private String secrets;
            private String nickname;
        }
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequestDto {
        private String email;
        private String password;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupDto {
        private String email;
        private String password;
        private String nickname;

        public static User dtoToEntity(SignupDto signupDto, String type) {
            BCryptService service = new BCryptService();
            String password = service.encodeBcrypt(signupDto.getPassword());
            return User.builder()
                    .files(new LinkedList<>())
                    .userParties(new LinkedList<>())
                    .email(signupDto.getEmail())
                    .password(password)
                    .nickname(signupDto.getNickname())
                    .sns(type).build();
        }
    }

    @Data
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupResponseDto {
        private long id;
        private String email;

        public static SignupResponseDto entityToDto(User user) {
            return SignupResponseDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponseDto {
        private String accessExpiredAt;
        private String refreshExpiredAt;
        private String accessToken;
        private String refreshToken;
        private String email;
    }
}
