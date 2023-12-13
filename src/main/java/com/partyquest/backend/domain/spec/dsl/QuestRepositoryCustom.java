package com.partyquest.backend.domain.spec.dsl;

public interface QuestRepositoryCustom {
    boolean updateIsDeleteQuestFromPartyID(Long partyID);
    boolean updateIsDeleteQuestFromUserID(long userID);
}
