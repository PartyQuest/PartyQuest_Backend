package com.partyquest.backend.domain.repository;

import com.partyquest.backend.domain.entity.UserParty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPartyRepository extends JpaRepository<UserParty,Long> {
}
