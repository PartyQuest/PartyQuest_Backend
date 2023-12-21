package com.partyquest.backend.service.logic;

import com.partyquest.backend.domain.dto.QuestDto;

import java.util.List;

public interface QuestService {
    QuestDto.CreateQuestDto.Response createQuest(long userID, QuestDto.CreateQuestDto.Request dto);
    List<QuestDto.ReadQuestDto.Response> readQuest(long userID, Long partyID, Long cursorID, Integer size,
                                                   String keyTitle, Boolean keyComplete, String keyNickname);
    void modifyQuest(QuestDto.ModifyQuestDto.Request request, long userID);
    void deleteQuest(Long userID, QuestDto.DeleteQuestDto.Request request);
}
