package com.gnimty.communityapiserver.domain.oauthinfo.repository;

import static com.gnimty.communityapiserver.domain.oauthinfo.entity.QOauthInfo.oauthInfo;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OauthInfoQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Boolean existsByEmailAndProvider(String email, Provider provider) {
		return queryFactory
			.selectOne()
			.from(oauthInfo)
			.where(oauthInfo.email.eq(email)
				.and(oauthInfo.provider.eq(provider)))
			.fetchFirst() != null;
	}

	public Boolean existsByMemberAndProvider(Member member, Provider provider) {
		return queryFactory
			.selectOne()
			.from(oauthInfo)
			.where(oauthInfo.member.id.eq(member.getId())
				.and(oauthInfo.provider.eq(provider)))
			.fetchFirst() != null;
	}
}
