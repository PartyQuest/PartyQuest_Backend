package com.partyquest.backend.domain.spec.dsl;


import com.partyquest.backend.domain.entity.Quest;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    @Override
    @Transactional
    public boolean updateIsDeleteQuestFromUserID(long userID) {
        try {
            jpaQueryFactory.update(quest1)
                    .set(quest1.isDelete, true)
                    .where(quest1.user.id.eq(userID))
                    .execute();
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<Quest> findByParentsQuestID(Long questID) {
        return Optional.ofNullable(
                jpaQueryFactory
                .select(quest1)
                .from(quest1)
                .join(quest1.quest, quest1)
                .where(quest1.quest.id.eq(questID))
                .fetchOne()
        );
    }
}
