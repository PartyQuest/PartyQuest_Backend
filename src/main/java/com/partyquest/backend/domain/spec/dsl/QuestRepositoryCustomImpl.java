package com.partyquest.backend.domain.spec.dsl;


import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.QQuest;
import com.partyquest.backend.domain.entity.Quest;
import com.partyquest.backend.domain.type.QuestType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.partyquest.backend.domain.entity.QQuest.quest;
import static com.partyquest.backend.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class QuestRepositoryCustomImpl implements QuestRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    private BooleanExpression titleContains(String title) {
        return title != null ? quest.title.contains(title) : null;
    }
    private BooleanExpression nicknameContains(String nickname) {
        return nickname != null ? quest.user.nickname.contains(nickname) : null;
    }
    private Boolean completeSelector(Boolean complete) {
        if(complete == null) {
            return false;
        }
        return complete;
    }

    @Override
    @Transactional
    public Page<RepositoryDto.QuestSummaryVO> findByQuestFromCursorID(Long cursorID, Integer size, Long partyID,
                                                                      String keyTitle, Boolean keyComplete, String keyNickname) {
        JPAQuery<RepositoryDto.QuestSummaryVO> query = jpaQueryFactory
                .select(
                        Projections.constructor(
                                RepositoryDto.QuestSummaryVO.class,
                                quest.id,quest.title, quest.description, quest.startTime, quest.endTime, quest.complete, user.nickname
                        )
                ).from(quest)
                .join(quest.user, user)
                .where(
                        quest.party.id.eq(partyID),
                        quest.type.eq(QuestType.NOTIFICATION),
                        quest.rootQuest.isNull(),
                        titleContains(keyTitle),
                        nicknameContains(keyNickname),
                        quest.complete.eq(completeSelector(keyComplete)),
                        quest.isDelete.eq(false)

                );
        if(cursorID != null) {
            query = query.where(quest.id.lt(cursorID));
        }
        if(size != null && size > 0) {
            query = query.limit(size+1);
        } else {
            query = query.limit(10+1);
        }
        List<RepositoryDto.QuestSummaryVO> result = query.orderBy(quest.id.desc()).fetch();

        boolean hasNext = result.size() > size;
        if(hasNext) {
            result.remove(size);
        }
        long total = hasNext ? result.size() + 1 : result.size();
        return new PageImpl<>(result, Pageable.unpaged(), total);
    }

    @Override
    @Transactional
    public boolean updateIsDeleteQuestFromPartyID(Long partyID) {
        try {

            jpaQueryFactory.update(quest)
                    .set(quest.isDelete, true)
                    .where(quest.party.id.eq(partyID))
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
            jpaQueryFactory.update(quest)
                    .set(quest.isDelete, true)
                    .where(quest.user.id.eq(userID))
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
                .select(quest)
                .from(quest)
                .join(quest.rootQuest, quest)
                .where(quest.rootQuest.id.eq(questID))
                .fetchOne()
        );
    }

    @Override
    @Transactional
    public boolean updateIsDeleteQuestFromRootQuest(Long questID) {
        try {
            jpaQueryFactory.update(quest).set(quest.isDelete, true).where(quest.rootQuest.id.eq(questID)).execute();
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    @Transactional
    public boolean updateIsDeleteQuestFromQuestID(Long questID) {
        try {
            jpaQueryFactory.update(quest).set(quest.isDelete, true).where(quest.id.eq(questID)).execute();
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
