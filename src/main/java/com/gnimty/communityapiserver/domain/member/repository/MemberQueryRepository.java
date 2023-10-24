package com.gnimty.communityapiserver.domain.member.repository;

import static com.gnimty.communityapiserver.domain.member.entity.QMember.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Boolean existsByEmail(String email) {
		return queryFactory
			.selectOne()
			.from(member)
			.where(member.email.eq(email))
			.fetchFirst() != null;
	}

	public Boolean existsById(Long id) {
		return queryFactory
			.selectOne()
			.from(member)
			.where(member.id.eq(id))
			.fetchFirst() != null;
	}
}
