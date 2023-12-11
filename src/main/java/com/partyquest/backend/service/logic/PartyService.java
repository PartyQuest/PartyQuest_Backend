package com.partyquest.backend.service.logic;

import com.partyquest.backend.config.exception.EmailNotFoundException;
import com.partyquest.backend.config.exception.NotAdminException;
import com.partyquest.backend.config.exception.PartyMemberException;
import com.partyquest.backend.domain.type.PartyMemberType;

import java.util.List;

import static com.partyquest.backend.domain.dto.PartyDto.*;

public interface PartyService {
    CreatePartyDto.Response createParty(CreatePartyDto.Request request, long makerID);
    List<ReadPartyDto.Response> readPartyDto(String master, String title, Long id);
    ReadPartyDto.Response readPartyDto(long id);
    ApplicationPartyDto.Response ApplicationParty(ApplicationPartyDto.Request requestDto, long userID);
    boolean deleteParty(List<Long> partyIds);
    List<ReadPartyMemberDto.Response> getMemberFromGrade(Long partyId, PartyMemberType grade);
    List<MembershipPartyDto.Response> getMembershipParties(long userId);
    ReadPartyDto.Response readPartySpecification(Long id);
    void AcceptPartyApplicator(ApplicationPartyDto.AcceptRequest dto, long masterID);
    void BannedAndRejectPartyMember(BannedMemberDto.Request dto, long masterID);
    void WithdrawParty(long userID, Long partyID);
    void ModifyPartyMemberGrade(long userID, ModifyMemberGradeDto.Request dto);
    void ModifyPartySpecification(long userID, ModifyPartySpecificationDto.Request dto);

}
