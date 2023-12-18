package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.Quest;

import java.util.Optional;

public interface QuestRepositoryCustom {
    boolean updateIsDeleteQuestFromPartyID(Long partyID);
    boolean updateIsDeleteQuestFromUserID(long userID);
    Optional<Quest> findByParentsQuestID(Long questID);
}
