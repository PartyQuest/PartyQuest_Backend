package com.partyquest.backend.domain.dto;


import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.type.PartyMemberType;
import lombok.*;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@Getter
@Setter
public class PartyDto {

    @Data
    @Builder
    public static class CreatePartyDto {

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Request {
            private String title;
            private String description;
            private Boolean isPublic;

            private String generateRandomString(int length) {
                String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
                StringBuilder sb = new StringBuilder(length);
                SecureRandom random = new SecureRandom();

                for (int i = 0; i < length; i++) {
                    int randomIndex = random.nextInt(characters.length());
                    char randomChar = characters.charAt(randomIndex);
                    sb.append(randomChar);
                }
                return sb.toString();
            }


            public static Party dtoToEntity(PartyDto.CreatePartyDto.Request request) {
                return Party.builder()
                        .files(new LinkedList<>())
                        .userParties(new LinkedList<>())
                        .accessCode(request.generateRandomString(16))
                        .capabilities(20)
                        .description(request.description)
                        .isPublic(request.isPublic)
                        .title(request.title)
                        .build();
            }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Response {
            private long id;
            private int capability;
            private String title;
            private String accessCode;
            private Boolean isPublic;

            public static Response entityToDto(Party party) {
                return Response.builder()
                        .id(party.getId())
                        .capability(party.getCapabilities())
                        .title(party.getTitle())
                        .isPublic(party.getIsPublic())
                        .accessCode(party.getAccessCode())
                        .build();
            }
        }

    }

    @Data
    @Builder
    public static class ReadPartyDto {
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Response {
            private long id;
            private String partyMaster;
            private String title;
            private String description;
            private int capability;
            private String thumbnailPath;

            public static Response entityToDto(Party party) {
                return Response.builder()
                        .id(party.getId())
                        .partyMaster(null)
                        .title(party.getTitle())
                        .description(party.getDescription())
                        .capability(party.getCapabilities())
                        .thumbnailPath(null)
                        .build();
            }

        }
    }
    @Data
    @Builder
    public static class ApplicationPartyDto {
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Request {
            private String partyName;
            private long partyId;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class AcceptRequest {
            private Long partyID;
            private List<Long> userID;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Response {
            private Long userPartyId;
            private Long userId;
            private Long partyId;
        }
    }

    @Data
    @Builder
    public static class ReadPartyMemberDto {
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Response {
            private long partyID;
            private long userID;
            private String filePath;
            private boolean registered;
            private String nickname;
            private PartyMemberType grade;
        }
    }

    @Data
    @Builder
    public static class MembershipPartyDto {
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Response {
            private Long partyID;
            private String partyThumbnailPath;
            private String partyTitle;
            private String partyMaster;
            private String memberGrade;
            private long partyMemberCnt;
        }
    }

    @Data
    @Builder
    public static class BannedMemberDto {
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Request {
            private Long partyID;
            private List<Long> userID;
        }
    }

    @Data
    @Builder
    public static class ModifyMemberGradeDto {

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ModifyMember {
            private long memberID;
            private PartyMemberType grade;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Request {
            private Long partyID;
            private List<ModifyMember> members;
        }
    }
}
