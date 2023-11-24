package com.partyquest.backend.domain.spec.dsl;

import com.partyquest.backend.domain.entity.File;
import com.partyquest.backend.domain.entity.Party;
import com.partyquest.backend.domain.entity.User;
import com.partyquest.backend.domain.type.FileType;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<Long, String> getUserImagePath(List<Long> ids) {
        List<Tuple> where = jpaQueryFactory
                .select(file.user.id, file.filePath)
                .from(file).innerJoin(file.user)
                .where(
                        file.type.eq(FileType.USER_THUMBNAIL),
                        file.user.id.in(ids)
                ).fetch();

        Map<Long,String> map = new HashMap<>();
        for(Tuple tuple : where) {
            map.put(tuple.get(file.user.id),tuple.get(file.filePath));
        }
        return map;
    }
}
