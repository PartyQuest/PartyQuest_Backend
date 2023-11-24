package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.entity.UserParty;
import com.partyquest.backend.domain.type.PartyMemberType;

import java.util.List;
import java.util.Optional;

public interface UserPartyRepositoryCustom {
    Optional<UserParty> existEntryUser(Party party, User user);
    List<UserParty> findByParty(Party party);
    Long countPartyMember(Party party);
    List<RepositoryDto.UserApplicatorRepositoryDto> findMemberFromGrade(Party party, PartyMemberType grade);
    List<RepositoryDto.MembershipDto> findMembershipUser(User user);
}
