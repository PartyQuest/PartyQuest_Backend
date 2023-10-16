package com.partyquest.backend.domain.dto;


import com.partyquest.backend.domain.entity.User;
import lombok.*;

@Data
@Builder
@Getter
@Setter
public class AuthDto {

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
        private String birth;

        public static User dtoToEntity(SignupDto signupDto, String type) {
            return User.builder()
                    .userParties(null)
                    .birth(signupDto.getBirth())
                    .email(signupDto.getEmail())
                    .password(signupDto.getPassword())
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
        private String accessToken;
        private String refreshToken;
        private String email;
    }
}
