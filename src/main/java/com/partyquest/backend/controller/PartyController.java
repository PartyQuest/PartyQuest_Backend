package com.partyquest.backend.controller;

import com.partyquest.backend.config.ResponseEntityFactory;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.service.logic.PartyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/party")
@Slf4j
public class PartyController {
    private final PartyService partyService;

    @Autowired
    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @PostMapping("")
    public ResponseEntity<?> createPartyController(@AuthenticationPrincipal long id,
                                                   @RequestBody PartyDto.CreatePartyDto.Request requestBody) {
        log.info(String.valueOf(id));
        log.info(String.valueOf(requestBody.getIsPublic()));
        PartyDto.CreatePartyDto.Response partyDto = partyService.createParty(requestBody, id);
        return ResponseEntityFactory.createResponse("/party/{id}", partyDto.getId(), partyDto);
    }

    @GetMapping("")
    public ResponseEntity<?> readPartyController(@AuthenticationPrincipal long id,
                                                 @RequestParam(value = "master",required = false) String master,
                                                 @RequestParam(value = "id",required = false) Long pId,
                                                 @RequestParam(value = "title",required = false) String title)
    {
        List<PartyDto.ReadPartyDto.Response> responses = partyService.readPartyDto(master, title, pId);
        return ResponseEntityFactory.okResponse(responses);
    }
}
