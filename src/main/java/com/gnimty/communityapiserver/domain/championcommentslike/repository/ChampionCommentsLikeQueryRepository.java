package com.gnimty.communityapiserver.domain.championcommentslike.repository;

import static com.gnimty.communityapiserver.domain.championcommentslike.entity.QChampionCommentsLike.*;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChampionCommentsLikeQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Boolean existsByMemberAndChampionComments(
		Member member,
		ChampionComments championComments
	) {
		return queryFactory
			.selectOne()
			.from(championCommentsLike)
			.where(championCommentsLike.member.id.eq(member.getId())
				.and(championCommentsLike.championComments.id.eq(championComments.getId())))
			.fetchFirst() != null;
	}
}
