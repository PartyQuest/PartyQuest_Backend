package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.Party;

import java.util.List;
import java.util.Optional;

public interface PartyRepositoryCustom {
    List<Party> findByTitle(String title);
    //List<PartyDto.ReadPartyDto.Response> getParties(String master, String title, Long id);
    List<Party> getParties(String master, String title, Long id);
    List<RepositoryDto.ReadPartiesVO> getPartiesTmp(String master, String title, Long id);
    Optional<Party> getParty(long id);
    String findMasterNameByParty(Party searchParty);
    RepositoryDto.ReadPartyVO getPartiesTmp(long id);
    void updateIsDeleteFromParty(Long partyID);
    List<Long> findByMyMasterPartyFromUserID(long userID);
}
