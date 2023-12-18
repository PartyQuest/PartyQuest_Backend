package com.partyquest.backend.service.logic;

import com.partyquest.backend.domain.dto.QuestDto;

public interface QuestService {
    QuestDto.CreateQuestDto.Response createQuest(long userID, QuestDto.CreateQuestDto.Request dto);
    
}
