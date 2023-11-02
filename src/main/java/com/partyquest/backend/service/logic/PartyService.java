package com.partyquest.backend.service.logic;

import com.partyquest.backend.domain.dto.PartyDto;

import java.util.List;

import static com.partyquest.backend.domain.dto.PartyDto.*;

public interface PartyService {
    CreatePartyDto.Response createPartyDto(CreatePartyDto.Request request, long makerID);
    List<ReadPartyDto.Response> readPartyDto(String master, String title, Long id);
    ReadPartyDto.Response readPartyDto(long id);


    boolean deleteParty(List<Long> partyIds);
}
