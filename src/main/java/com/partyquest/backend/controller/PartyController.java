package com.partyquest.backend.controller;

import com.partyquest.backend.config.ResponseEntityFactory;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.service.logic.PartyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        PartyDto.CreatePartyDto.Response partyDto = partyService.createPartyDto(requestBody, id);
        return ResponseEntityFactory.createResponse("/party/{id}", partyDto.getId(), partyDto);
    }
}
