package com.gnimty.communityapiserver.domain.memberlike.repository;

import static com.gnimty.communityapiserver.domain.memberlike.entity.QMemberLike.memberLike;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberLikeQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Boolean existsBySourceMemberAndTargetMember(Member source, Member target) {
		return queryFactory
			.selectOne()
			.from(memberLike)
			.where(memberLike.sourceMember.id.eq(source.getId())
				.and(memberLike.targetMember.id.eq(target.getId())))
			.fetchFirst() != null;
	}
}
