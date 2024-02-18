package com.gnimty.communityapiserver.domain.block.repository;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.gnimty.communityapiserver.domain.block.entity.QBlock.block;

@Repository
@RequiredArgsConstructor
public class BlockQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Boolean existsByBlockerAndBlocked(Member blocker, Member blocked) {
        return queryFactory
            .selectOne()
            .from(block)
            .where(block.blocker.id.eq(blocker.getId())
                .and(block.blocked.id.eq(blocked.getId())))
            .fetchFirst() != null;
    }

    public Boolean existsByBlockerAndBlocked(Long blockerId, Long blockedId) {
        return queryFactory
            .selectOne()
            .from(block)
            .where(block.blocker.id.eq(blockerId)
                .and(block.blocked.id.eq(blockedId)))
            .fetchFirst() != null;
    }
}
