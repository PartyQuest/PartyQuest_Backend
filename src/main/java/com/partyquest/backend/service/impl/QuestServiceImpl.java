package com.partyquest.backend.service.impl;

import com.partyquest.backend.config.exception.*;
import com.partyquest.backend.domain.dto.QuestDto;
import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.Quest;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.repository.PartyRepository;
import com.partyquest.backend.domain.repository.QuestRepository;
import com.partyquest.backend.domain.repository.UserPartyRepository;
import com.partyquest.backend.domain.repository.UserRepository;
import com.partyquest.backend.domain.type.QuestType;
import com.partyquest.backend.service.logic.QuestService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
                            .rootQuest(null)
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
                            .rootQuest(parentsQuest)
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

    @Override
    public List<QuestDto.ReadQuestDto.Response> readQuest(long userID, Long partyID, Long cursorID, Integer size,
                                                    String keyTitle, Boolean keyComplete, String keyNickname) {
        isPartyMember(userID, partyID);
        List<RepositoryDto.QuestSummaryVO> voList =
                questRepository.findByQuestFromCursorID(cursorID, size, partyID, keyTitle, keyComplete, keyNickname).getContent();

        return voList.stream().map(questSummaryVO -> QuestDto.ReadQuestDto.Response.builder()
                .questID(questSummaryVO.getQuestID())
                .title(questSummaryVO.getTitle())
                .description(questSummaryVO.getDescription())
                .startTime(questSummaryVO.getStartTime())
                .endTime(questSummaryVO.getEndTime())
                .complete(questSummaryVO.getComplete())
                .writer(questSummaryVO.getUserName())
                .build()
        ).toList();
    }

    @Override
    public void modifyQuest(QuestDto.ModifyQuestDto.Request request, long userID) {
        isUser(userID);
        isPartyMember(userID,request.getPartyID());
        isWriter(userID,request.getQuestID());

        Quest quest = questRepository.findById(request.getQuestID()).get();
        quest.setTitle(request.getTitle());
        quest.setDescription(request.getDescription());
        quest.setStartTime(request.getStartTime());
        quest.setEndTime(request.getEndTime());
        questRepository.save(quest);
    }

    @Override
    public void deleteQuest(Long userID, QuestDto.DeleteQuestDto.Request request) {
        isUser(userID);
        isPartyMember(userID, request.getPartyID());
        isAuthentication(userID, request.getPartyID(), request.getQuestID());

        questRepository.updateIsDeleteQuestFromRootQuest(request.getQuestID());
        questRepository.updateIsDeleteQuestFromQuestID(request.getQuestID());

    }

    @Transactional
    private void isAuthentication(Long userID, Long partyID, Long questID) {
        User user = userRepository.findById(userID).get();
        Quest quest = questRepository.findById(questID).get();

        if(!userPartyRepository.existsMasterFromUserParty(userID,partyID) && (user.getId() != quest.getUser().getId())) {
            throw new QuestInputErrorException("Invalid authentication",ErrorCode.QUEST_INPUT_ERROR);
        }
    }

    @Transactional
    private void isWriter(Long userID, Long questID) {
        User user = userRepository.findById(userID).get();
        Quest quest = questRepository.findById(questID).get();
        if(user.getId() != quest.getUser().getId()) {
            throw new QuestInputErrorException("Invalid Writer",ErrorCode.QUEST_INPUT_ERROR);
        }

    }
    private void isUser(long userID) {
        if(!userRepository.existsById(userID))
            throw new EmailNotFoundException("NOT FOUND USER", ErrorCode.EMAIL_NOT_FOUND);
    }
    private void isParty(Long partyID) {
        if(!partyRepository.existsById(partyID))
            throw new PartyNotFoundException("NOT FOUND PARTY", ErrorCode.PARTY_NOT_FOUND);
    }

    private void isPartyMember(long userID, Long partyID) {
        if(!userPartyRepository.existsByUsers(List.of(userID),partyID))
            throw new PartyMemberException("NOT PARTY MEMBER",ErrorCode.PARTY_MEMBER_ERROR);
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
