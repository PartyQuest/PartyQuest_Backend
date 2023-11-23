package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;

import java.util.List;

public interface FileRepositoryCustom {
    List<File> findByParty(Party party);
    File getUserThumbnailPath(User user);
}
