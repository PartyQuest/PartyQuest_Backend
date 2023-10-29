package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.UserParty;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import static com.partyquest.backend.domain.entity.QUserParty.userParty;


@Repository
@RequiredArgsConstructor
public class UserPartyRepositoryCustomImpl implements UserPartyRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public UserParty findByParty(Party party) {
        return jpaQueryFactory.selectFrom(userParty).where(userParty.party.eq(party)).fetchOne();
    }
}
