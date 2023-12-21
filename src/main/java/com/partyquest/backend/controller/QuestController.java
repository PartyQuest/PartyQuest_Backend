package com.partyquest.backend.controller;

import com.partyquest.backend.config.ResponseEntityFactory;
import com.partyquest.backend.service.logic.QuestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("")
    public ResponseEntity<?> readQuest(@AuthenticationPrincipal long id,
                                       @RequestParam Long partyID ,
                                       @RequestParam(required = false) Long cursorID,
                                       @RequestParam(required = false) Integer size,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(required = false) String writer,
                                       @RequestParam(required = false) Boolean complete
                                       )
    {
        if(size == null) size = 10;
        List<ReadQuestDto.Response> responses = questService.readQuest(id, partyID, cursorID, size, title, complete, writer);
        return ResponseEntityFactory.okResponse(responses);
    }

    @PatchMapping("")
    public ResponseEntity<?> modifyQuest(@AuthenticationPrincipal long id,
                                         @RequestBody ModifyQuestDto.Request request)
    {
        questService.modifyQuest(request,id);
        return ResponseEntityFactory.noResponse();
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteQuest(@AuthenticationPrincipal long id, @RequestBody DeleteQuestDto.Request request) {
        questService.deleteQuest(id,request);
        return ResponseEntityFactory.noResponse();
    }
}
