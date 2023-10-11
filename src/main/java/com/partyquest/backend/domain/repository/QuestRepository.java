package com.partyquest.backend.domain.repository;

import com.partyquest.backend.domain.entity.Quest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest,Long> {
}
