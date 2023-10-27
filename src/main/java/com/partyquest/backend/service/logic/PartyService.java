package com.partyquest.backend.service.logic;

import com.partyquest.backend.domain.dto.PartyDto;
import static com.partyquest.backend.domain.dto.PartyDto.CreatePartyDto;

public interface PartyService {
    CreatePartyDto.Response createPartyDto(CreatePartyDto.Request request, long makerID);
//    List<PartyDto.ReadPartyDto.Response> readPartyDto();
//    PartyDto.ReadPartyDto.Response readPartyDto(long id);


}
