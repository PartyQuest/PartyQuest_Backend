package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.type.FileType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.partyquest.backend.domain.entity.QFile.file;

@Repository
@RequiredArgsConstructor
public class FileRepositoryCustomImpl implements FileRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public List<File> findByParty(Party party) {
        return jpaQueryFactory.selectFrom(file).where(file.party.eq(party)).fetch();
    }

    @Override
    public File getUserThumbnailPath(User user) {
        return jpaQueryFactory
                .selectFrom(file)
                .where(
                        file.user.eq(user),
                        file.type.eq(FileType.USER_THUMBNAIL)
                ).fetchOne();
    }
}
