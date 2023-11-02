package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.Party;

import java.util.List;

public interface FileRepositoryCustom {
    List<File> findByParty(Party party);
}
