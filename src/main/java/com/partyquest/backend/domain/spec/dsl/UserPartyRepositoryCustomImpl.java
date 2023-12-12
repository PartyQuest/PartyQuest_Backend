package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.entity.UserParty;
import com.partyquest.backend.domain.type.FileType;
import com.partyquest.backend.domain.type.PartyMemberType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

import static com.partyquest.backend.domain.entity.QUserParty.userParty;
import static com.partyquest.backend.domain.entity.QUser.user;
import static com.partyquest.backend.domain.entity.QFile.file;
import static com.partyquest.backend.domain.entity.QParty.party;


@Repository
@RequiredArgsConstructor
public class UserPartyRepositoryCustomImpl implements UserPartyRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<UserParty> findByParty(Party party) {
        return jpaQueryFactory.selectFrom(userParty).where(userParty.party.eq(party)).fetch();
    }

    @Override
    public Long countPartyMember(Party party) {
        return jpaQueryFactory
                .select(userParty.count())
                .from(userParty)
                .where(
                        userParty.party.eq(party),
                        userParty.registered.eq(true))
                .fetchOne();
    }

    @Override
    public List<RepositoryDto.PartyMemberVO> findMemberFromGrade(Long partyID, PartyMemberType grade) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                            RepositoryDto.PartyMemberVO.class,
                                user.id,
                                file.filePath,
                                userParty.registered,
                                user.nickname,
                                userParty.memberGrade
                        )
                )
                .from(userParty)
                .join(userParty.party, party)
                .join(userParty.user, user)
                .join(user.files, file)
                .where(
                        user.isDelete.eq(false),
                        userParty.isDelete.eq(false),
                        file.isDelete.eq(false),
                        party.isDelete.eq(false),
                        party.id.eq(partyID),
                        gradeEq(grade)
                ).fetch();
    }

    private BooleanExpression gradeEq(PartyMemberType grade) {
        return grade != null? userParty.memberGrade.eq(grade) : null;
    }

    @Override
    public List<RepositoryDto.MembershipDto> findMembershipUser(User users) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                RepositoryDto.MembershipDto.class,
                                party.id,
                                file.filePath,
                                party.title,
                                user.nickname,
                                userParty.memberGrade,
                                Expressions.as(
                                        JPAExpressions
                                                .select(userParty.count())
                                                .from(userParty)
                                                .where(userParty.party.eq(party)),"cnt"
                                )
                        )
                )
                .from(userParty)
                .join(userParty.user,user)
                .join(userParty.party,party)
                .join(party.files,file)
                .where(userParty.user.eq(users),file.type.eq(FileType.PARTY_THUMBNAIL))
                .fetch();
    }

    @Override
    @Transactional
    public boolean updateAcceptApplicator(List<Long> userID) {
        try {
            jpaQueryFactory
                    .update(userParty)
                    .set(userParty.memberGrade, PartyMemberType.MEMBER)
                    .set(userParty.registered, true)
                    .where(
                            userParty.user.in(
                                    JPAExpressions
                                            .selectFrom(user)
                                            .where(user.id.in(userID))
                            )
                    ).execute();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return true;
    }

    @Override
    @Transactional
    public boolean updateRegisterAndisDeleteFalse(Long partyID, List<Long> userID) {
        try {
            jpaQueryFactory
                    .update(userParty)
                    .set(userParty.registered, false)
                    .set(userParty.isDelete, true)
                    .where(
                            userParty.user.id.in(userID),
                            userParty.party.id.eq(partyID)
                    ).execute();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateUserPartyMemberGrade(Long partyID, long userID, PartyMemberType grade) {
        try {
            if(grade == PartyMemberType.ADMIN || grade == PartyMemberType.MASTER) {
                jpaQueryFactory
                        .update(userParty)
                        .set(userParty.memberGrade, grade)
                        .set(userParty.partyAdmin,true)
                        .where(
                                userParty.party.id.eq(partyID),
                                userParty.user.id.eq(userID)
                        ).execute();
            } else {
                jpaQueryFactory
                        .update(userParty)
                        .set(userParty.memberGrade, grade)
                        .where(
                                userParty.party.id.eq(partyID),
                                userParty.user.id.eq(userID)
                        ).execute();
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    @Transactional
    public boolean updateIsDeletePartyMember(Long partyID) {
        try {

            jpaQueryFactory
                    .update(userParty)
                    .set(userParty.isDelete, true)
                    .where(userParty.party.id.eq(partyID)).execute();

            return true;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean existsMasterFromUserParty(long userID, Long partyID) {
        UserParty fetch = jpaQueryFactory
                .selectFrom(userParty)
                .where(
                        userParty.party.id.eq(partyID),
                        userParty.user.id.eq(userID),
                        userParty.memberGrade.eq(PartyMemberType.MASTER)
                ).fetchOne();
        return fetch != null;
    }

    @Override
    public boolean isMasterAndAdminUser(User user, Party party) {
        return false;
    }

    @Override
    public boolean isMasterAndAdminUserTmp(Long userID, Long partyID) {
        UserParty fetch = jpaQueryFactory
                .selectFrom(userParty)
                .join(userParty.party, party)
                .join(userParty.user, user)
                .where(
                        party.id.eq(partyID),
                        user.id.eq(userID),
                        userParty.partyAdmin.eq(true)
                ).fetchOne();
        return fetch != null;
    }

    @Override
    public boolean isApplicationUser(List<Long> userID, Long PartyID) {
        Long one = jpaQueryFactory.select(userParty.count())
                .from(userParty)
//                .join(userParty.party, party)
//                .join(userParty.user, user)
                .where(
                        userParty.party.id.eq(PartyID),
                        userParty.user.id.in(userID),
                        userParty.registered.eq(false),
                        userParty.memberGrade.eq(PartyMemberType.NO_MEMBER)
                ).fetchOne();
        return one == userID.size();
    }

    @Override
    public boolean existsByUsers(List<Long> userID, Long PartyID) {
        Long one = jpaQueryFactory
                .select(userParty.count())
                .from(userParty)
                .where(
                        userParty.party.id.eq(PartyID),
                        userParty.user.id.in(userID)
                ).fetchOne();
        return one == userID.size();
    }

    @Override
    public Optional<UserParty> existEntryUser(Party party, User user) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(userParty)
                .where(
                        userParty.party.eq(party),
                        userParty.user.eq(user)
//                        userParty.isDelete.eq(false)
                ).fetchOne());
    }
}
