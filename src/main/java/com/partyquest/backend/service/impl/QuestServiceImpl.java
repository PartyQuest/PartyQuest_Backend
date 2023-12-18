package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.exception.*;
import com.partyquest.backend.domain.dto.QuestDto;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.Quest;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.repository.PartyRepository;
import com.partyquest.backend.domain.repository.QuestRepository;
import com.partyquest.backend.domain.repository.UserPartyRepository;
import com.partyquest.backend.domain.repository.UserRepository;
import com.partyquest.backend.domain.type.QuestType;
import com.partyquest.backend.service.logic.QuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class QuestServiceImpl implements QuestService {
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final UserPartyRepository userPartyRepository;
    private final QuestRepository questRepository;

    @Autowired
    public QuestServiceImpl(PartyRepository partyRepository, UserRepository userRepository, UserPartyRepository userPartyRepository, QuestRepository questRepository) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
        this.userPartyRepository = userPartyRepository;
        this.questRepository = questRepository;
    }

    @Override
    public QuestDto.CreateQuestDto.Response createQuest(long userID, QuestDto.CreateQuestDto.Request dto) {
        isUser(userID);
        isParty(dto.getPartyID());
        gradeChecker(dto.getType(),userID,dto.getPartyID());

        Quest quest = null;
        User user = userRepository.findById(userID).get();
        Party party = partyRepository.findById(dto.getPartyID()).get();
        if(dto.getQuestID() == null && dto.getType().equals(QuestType.NOTIFICATION)) {
            quest = questRepository.save(
                    Quest.builder()
                            .quest(null)
                            .complete(false)
                            .deleteHide(false)
                            .description(dto.getDescription())
                            .endTime(dto.getEndTime())
                            .startTime(dto.getStartTime())
                            .quests(new ArrayList<>())
                            .title(dto.getTitle())
                            .type(dto.getType())
                            .user(user)
                            .party(party)
                            .build()
            );
        } else if(dto.getQuestID() != null && dto.getType().equals(QuestType.SUBMIT)) {
            Quest parentsQuest = questRepository.findById(dto.getQuestID()).orElseThrow(
                    () -> new QuestInputErrorException("Invalid Parents Quest", ErrorCode.QUEST_INPUT_ERROR));
            quest = questRepository.save(
                    Quest.builder()
                            .quests(new ArrayList<>())
                            .complete(true)
                            .deleteHide(false)
                            .user(user)
                            .party(party)
                            .quest(parentsQuest)
                            .type(dto.getType())
                            .title(dto.getTitle())
                            .startTime(dto.getStartTime())
                            .endTime(dto.getEndTime())
                            .description(dto.getDescription())
                            .build()
            );
            parentsQuest.getQuests().add(quest);
        } else {
            throw new QuestInputErrorException("Invalid quest input",ErrorCode.QUEST_INPUT_ERROR);
        }
        user.getQuests().add(quest);
        party.getQuests().add(quest);

        return QuestDto.CreateQuestDto.Response.builder()
                .complete(quest.getComplete())
                .id(quest.getId())
                .description(quest.getDescription())
                .endTime(quest.getEndTime())
                .startTime(quest.getStartTime())
                .title(quest.getTitle())
                .type(quest.getType())
                .build();
    }


    private void isUser(long userID) {
        if(!userRepository.existsById(userID))
            throw new EmailNotFoundException("NOT FOUND USER", ErrorCode.EMAIL_NOT_FOUND);
    }
    private void isParty(Long partyID) {
        if(!partyRepository.existsById(partyID))
            throw new PartyNotFoundException("NOT FOUND PARTY", ErrorCode.PARTY_NOT_FOUND);
    }

    private void gradeChecker(QuestType type, long userID, Long partyID) {
        if(type.equals(QuestType.NOTIFICATION)) {
            if(!userPartyRepository.existsMasterFromUserParty(userID, partyID)) {
                throw new NotAdminException("NOT MASTER", ErrorCode.ACCESS_DENIED);
            }
        } else if (type.equals(QuestType.SUBMIT)) {
            if(!userPartyRepository.existsByUsers(List.of(userID),partyID))
                throw new PartyMemberException("NOT PARTY MEMBER",ErrorCode.PARTY_MEMBER_ERROR);
        }
    }
}
