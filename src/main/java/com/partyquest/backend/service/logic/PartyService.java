package com.partyquest.backend.service.logic;

import com.partyquest.backend.domain.type.PartyMemberType;

import java.util.HashMap;
import java.util.List;

import static com.partyquest.backend.domain.dto.PartyDto.*;

public interface PartyService {
    CreatePartyDto.Response createParty(CreatePartyDto.Request request, long makerID);
    List<ReadPartyDto.Response> readPartyDto(String master, String title, Long id);
    ReadPartyDto.Response readPartyDto(long id);
    ApplicationPartyDto.Response ApplicationParty(ApplicationPartyDto.Request requestDto, long userID);
    boolean deleteParty(List<Long> partyIds);

    HashMap<String, Object> getMemberFromGrade(Long partyId, PartyMemberType grade);
    List<MembershipPartyDto.Response> getMembershipParties(long userId);

    ReadPartyDto.Response readPartySpecification(Long id);
    void AcceptPartyApplicator(List<Long> userID);

}
