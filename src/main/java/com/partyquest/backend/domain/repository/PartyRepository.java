package com.partyquest.backend.domain.repository;

import com.partyquest.backend.domain.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party,Long> {
}
