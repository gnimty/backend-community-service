package com.gnimty.communityapiserver.domain.championcommentsreport.reposiotry;

import static com.gnimty.communityapiserver.domain.championcommentsreport.entity.QChampionCommentsReport.championCommentsReport;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChampionCommentsReportQueryRepository {

    private final JPAQueryFactory queryFactory;

    public boolean existsByMemberAndChampionComments(Member member, ChampionComments championComments) {
        return queryFactory.selectOne()
            .from(championCommentsReport)
            .where(memberIdEq(member), championCommentsIdEq(championComments))
            .fetchFirst() != null;
    }

    private BooleanExpression championCommentsIdEq(ChampionComments championComments) {
        return championCommentsReport.championComments.id.eq(championComments.getId());
    }

    private BooleanExpression memberIdEq(Member member) {
        return championCommentsReport.member.id.eq(member.getId());
    }
}
