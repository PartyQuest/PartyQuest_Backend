package com.partyquest.backend.domain.repository;

import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.entity.UserParty;
import com.partyquest.backend.domain.spec.dsl.UserPartyRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserPartyRepository extends JpaRepository<UserParty,Long>, UserPartyRepositoryCustom {

}
