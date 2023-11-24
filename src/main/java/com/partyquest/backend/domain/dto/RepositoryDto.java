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
        private String nickname;
        private long id;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserThumbnailPathDto {
        private Long userId;
        private String filePath;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MembershipDto {
        private String partyThumbnailPath;
        private String partyTitle;
        private String partyMaster;
        private long partyMemberCnt;
    }
}
