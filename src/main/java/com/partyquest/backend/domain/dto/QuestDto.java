package com.partyquest.backend.domain.dto;

import com.partyquest.backend.domain.type.QuestType;
import lombok.*;

@Data
@Builder
@Getter
@Setter
public class QuestDto {
    @Data
    @Builder
    public static class CreateQuestDto {
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Request {
            private Long partyID;
            private Long questID;
            private String title;
            private String description;
            private String startTime;
            private String endTime;
            private QuestType type;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Response {
            private String title;
            private String description;
            private String startTime;
            private String endTime;
            private QuestType type;
            private Boolean complete;
            private long id;
        }
    }

    @Data
    @Builder
    public static class ReadQuestDto {

    }

    @Data
    @Builder
    public static class ModifyQuestDto {

    }

    @Data
    @Builder
    public static class DeleteQuestDto {

    }
}
