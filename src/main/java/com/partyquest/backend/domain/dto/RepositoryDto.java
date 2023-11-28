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
        private String filePath;
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadPartiesVO {
        private String partyThumbnailPath;
        private String partyTitle;
        private long partyId;
        private String partyMaster;
        private long partyMemberCnt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReadPartyVO {
        private String partyThumbnailPath;
        private String description;
        private String partyTitle;
        private long partyId;
        private String partyMaster;
        private long partyMemberCnt;
    }
}
