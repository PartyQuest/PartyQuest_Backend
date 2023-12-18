package com.partyquest.backend.controller;

import com.partyquest.backend.config.ResponseEntityFactory;
import com.partyquest.backend.service.logic.QuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.partyquest.backend.domain.dto.QuestDto.*;

@RestController
@RequestMapping("/quest")
@Slf4j
public class QuestController {
    private final QuestService questService;

    @Autowired
    public QuestController(QuestService questService) {
        this.questService = questService;
    }


    @PostMapping("")
    public ResponseEntity<?> createQuest(@AuthenticationPrincipal long id, @RequestBody CreateQuestDto.Request dto)
    {
        CreateQuestDto.Response response = questService.createQuest(id, dto);
        return ResponseEntityFactory.createResponse("/quest/{id}", response.getId(),response);
    }
}
