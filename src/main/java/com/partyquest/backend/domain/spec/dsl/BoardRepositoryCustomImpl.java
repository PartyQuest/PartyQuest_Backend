package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.QBoard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import static com.partyquest.backend.domain.entity.QBoard.board1;
@RequiredArgsConstructor
@Repository
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    @Transactional
    public boolean updateIsDeleteFromUserID(long userID) {
        try {
            jpaQueryFactory
                    .update(board1)
                    .set(board1.isDelete, true)
                    .where(
                            board1.writer.id.eq(userID)
                    ).execute();
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
