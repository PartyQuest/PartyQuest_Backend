package com.partyquest.backend.domain.spec.dsl;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.partyquest.backend.domain.entity.QQuest.quest1;

@Repository
@RequiredArgsConstructor
public class QuestRepositoryCustomImpl implements QuestRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public boolean updateIsDeleteQuestFromPartyID(Long partyID) {
        try {

            jpaQueryFactory.update(quest1)
                    .set(quest1.isDelete, true)
                    .where(quest1.party.id.eq(partyID))
                    .execute();

            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
