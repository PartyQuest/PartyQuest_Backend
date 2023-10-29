package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.Party;

import java.util.List;
import java.util.Optional;

public interface PartyRepositoryCustom {
    List<Party> findByTitle(String title);
    Optional<List<Party>> getParty(String master, String title);
    Optional<Party> getParty(long id);
}
