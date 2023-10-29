package com.partyquest.backend.domain.repository;

import com.partyquest.backend.domain.entity.Quest;
import com.partyquest.backend.domain.spec.dsl.QuestRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestRepository extends JpaRepository<Quest,Long>, QuestRepositoryCustom {
}
