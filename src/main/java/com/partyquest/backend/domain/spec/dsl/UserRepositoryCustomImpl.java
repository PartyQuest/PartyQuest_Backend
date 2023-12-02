package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.partyquest.backend.domain.entity.QUser.user;

@RequiredArgsConstructor
@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(user).where(user.email.eq(email)).fetchOne());
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        User fetchOne = jpaQueryFactory.selectFrom(user)
                .where(
                    user.email.eq(email).and(
                            user.password.eq(password)
                    )
                )
                .fetchOne();

        return Optional.ofNullable(fetchOne);
    }

    @Override
    public boolean isUser(List<Long> userID) {
        return jpaQueryFactory.select(user.count()).from(user).where(user.id.in(userID)).fetchOne() == userID.size();
    }

}
