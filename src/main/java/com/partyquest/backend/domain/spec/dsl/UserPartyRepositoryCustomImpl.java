package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.entity.UserParty;
import com.partyquest.backend.domain.type.FileType;
import com.partyquest.backend.domain.type.PartyMemberType;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
    public List<RepositoryDto.UserApplicatorRepositoryDto> findMemberFromGrade(Party party,PartyMemberType grade) {
        return jpaQueryFactory
                .select(
                        Projections.fields(RepositoryDto.UserApplicatorRepositoryDto.class,
                                userParty.registered,
                                userParty.user.nickname,
                                userParty.user.id)
                )
                .from(userParty).innerJoin(userParty.user)
                .where(
                        userParty.memberGrade.eq(grade),
                        userParty.party.eq(party)
                        )
                .fetch();
    }

    @Override
    public List<RepositoryDto.MembershipDto> findMembershipUser(User users) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                RepositoryDto.MembershipDto.class,
                                file.filePath,party.title,user.nickname,
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
    public Optional<UserParty> existEntryUser(Party party, User user) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(userParty)
                .where(
                        userParty.party.eq(party),
                        userParty.user.eq(user)
                ).fetchOne());
    }
}
