package com.partyquest.backend.domain.dto;


import com.partyquest.backend.domain.type.PartyMemberType;
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
    public static class QuestSummaryVO {
        private Long questID;
        private String title;
        private String description;
        private String startTime;
        private String endTime;
        private Boolean complete;
        private String userName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartyMemberVO {
        private long userID;
        private String filePath;
        private boolean registered;
        private String nickname;
        private PartyMemberType grade;
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
        private Long id;
        private String partyThumbnailPath;
        private String partyTitle;
        private String partyMaster;
        private PartyMemberType grade;
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
