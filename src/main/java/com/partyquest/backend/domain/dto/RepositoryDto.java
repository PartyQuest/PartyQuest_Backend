package com.partyquest.backend.domain.dto;


import lombok.*;

@Data
@Builder
@Getter
@Setter
public class RepositoryDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserApplicatorRepositoryDto {
        private boolean registered;
        private boolean nickname;
    }
}
