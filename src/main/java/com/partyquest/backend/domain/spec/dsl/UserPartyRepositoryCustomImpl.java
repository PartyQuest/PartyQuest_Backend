package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.dto.RepositoryDto;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.entity.UserParty;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

import static com.partyquest.backend.domain.entity.QUserParty.userParty;
import static com.partyquest.backend.domain.entity.QUser.user;


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
    public List<RepositoryDto.UserApplicatorRepositoryDto> findApplicators(Party party) {
        return jpaQueryFactory
                .select(Projections.fields(RepositoryDto.UserApplicatorRepositoryDto.class, userParty.registered,userParty.user.nickname))
                .from(userParty,user).where(userParty.user.eq(user)).fetch();
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
