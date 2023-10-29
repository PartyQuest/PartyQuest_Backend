package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.UserParty;

public interface UserPartyRepositoryCustom {
    UserParty findByParty(Party party);
}
