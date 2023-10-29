package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.UserParty;
import org.springframework.stereotype.Repository;

@Repository
public class UserPartyRepositoryCustomImpl implements UserPartyRepositoryCustom{
    @Override
    public UserParty findByParty(Party party) {
        return null;
    }
}
