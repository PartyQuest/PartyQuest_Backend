package com.partyquest.backend.controller;

import com.partyquest.backend.config.JsonEnumTypeConfig;
import com.partyquest.backend.config.ResponseEntityFactory;
import com.partyquest.backend.config.exception.ErrorCode;
import com.partyquest.backend.config.exception.PartyMemberException;
import com.partyquest.backend.domain.dto.PartyDto;
import com.partyquest.backend.domain.dto.ResponseWrapper;
import com.partyquest.backend.domain.type.PartyMemberType;
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
        if(master == null && title == null && pId != null) {
            return ResponseEntityFactory.okResponse(partyService.readPartySpecification(pId));
        }
        return ResponseEntityFactory.okResponse(partyService.readPartyDto(master, title, pId));
    }

    @PostMapping("/application")
    public ResponseEntity<?> ApplicationPartyController(@AuthenticationPrincipal long id,
                                                        @RequestBody PartyDto.ApplicationPartyDto.Request dto)
    {
        log.info(dto.toString());
        PartyDto.ApplicationPartyDto.Response response = partyService.ApplicationParty(dto,id);
        return ResponseEntityFactory.okResponse(response);
    }

    @GetMapping("/member")
    public ResponseEntity<?> FindMemberFromGradeController
            (
                @AuthenticationPrincipal long id,
                @RequestParam(value = "grade", required = false) String grade,
                @RequestParam(value = "partyID") Long partyID
            )
    {
        if(grade == null) {
            return ResponseEntityFactory.okResponse(partyService.getMemberFromGrade(partyID, null));
        }
        log.info(JsonEnumTypeConfig.fromString(PartyMemberType.class,grade).toString()+" "+partyID);
        return ResponseEntityFactory.okResponse(partyService.getMemberFromGrade(partyID, JsonEnumTypeConfig.fromString(PartyMemberType.class, grade)));
    }

    @GetMapping("/my-parties")
    public ResponseEntity<?> FindMyPartiesController(@AuthenticationPrincipal long id) {
        List<PartyDto.MembershipPartyDto.Response> parties = partyService.getMembershipParties(id);
        return ResponseEntityFactory.okResponse(parties);
    }

    @PatchMapping("/application")
    public ResponseEntity<?> AcceptPartyApplicator(@AuthenticationPrincipal long id,
                                                   @RequestBody PartyDto.ApplicationPartyDto.AcceptRequest dto)
    {
        partyService.AcceptPartyApplicator(dto,id);
        return ResponseEntityFactory.noResponse();
    }

    @DeleteMapping("/member")
    public ResponseEntity<?> BannedMember(@AuthenticationPrincipal long id,
                                          @RequestBody PartyDto.BannedMemberDto.Request dto) {
        if(dto.getUserID().contains(id)) {
            throw new PartyMemberException("DO NOT BANNED YOURSELF", ErrorCode.PARTY_MEMBER_ERROR);
        }
        partyService.BannedAndRejectPartyMember(dto,id);
        return ResponseEntityFactory.noResponse();
    }

    @DeleteMapping("/member/my-parties")
    public ResponseEntity<?> WithdrawParty(@AuthenticationPrincipal long id, @RequestParam Long partyID) {
        partyService.WithdrawParty(id, partyID);
        return ResponseEntityFactory.noResponse();
    }

    @PatchMapping("/member")
    public ResponseEntity<?> ModifyMemberGrade(@AuthenticationPrincipal long id,
                                               @RequestBody PartyDto.ModifyMemberGradeDto.Request dto) {
        partyService.ModifyPartyMemberGrade(id,dto);
        return ResponseEntityFactory.noResponse();
    }
}
