package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.Quest;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface QuestRepositoryCustom {
    boolean updateIsDeleteQuestFromPartyID(Long partyID);
    boolean updateIsDeleteQuestFromUserID(long userID);
    Optional<Quest> findByParentsQuestID(Long questID);
    Page<RepositoryDto.QuestSummaryVO> findByQuestFromCursorID(Long cursorID, Integer size, Long partyID,
                                                               String keyTitle, Boolean keyComplete, String keyNickname);

    boolean updateIsDeleteQuestFromRootQuest(Long questID);
    boolean updateIsDeleteQuestFromQuestID(Long questID);
}
