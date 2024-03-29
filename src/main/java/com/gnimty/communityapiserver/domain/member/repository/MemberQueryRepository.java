package com.gnimty.communityapiserver.domain.member.repository;

import static com.gnimty.communityapiserver.domain.introduction.entity.QIntroduction.introduction;
import static com.gnimty.communityapiserver.domain.member.entity.QMember.member;
import static com.gnimty.communityapiserver.domain.prefergamemode.entity.QPreferGameMode.preferGameMode;
import static com.gnimty.communityapiserver.domain.riotaccount.entity.QRiotAccount.riotAccount;
import static com.gnimty.communityapiserver.domain.schedule.entity.QSchedule.schedule;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OtherProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
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
			.where(member.email.eq(email), member.deleted.isFalse())
			.fetchFirst() != null;
	}

	public Boolean existsById(Long id) {
		return queryFactory
			.selectOne()
			.from(member)
			.where(member.id.eq(id), member.deleted.isFalse())
			.fetchFirst() != null;
	}

	public OtherProfileServiceResponse findOtherById(Long id) {
		List<Schedule> schedules = queryFactory
			.selectFrom(schedule)
			.join(schedule.member, member).on(schedule.member.id.eq(member.id), schedule.deleted.isFalse())
			.where(schedule.member.id.eq(id))
			.fetch();
		Optional<Introduction> mainIntroduction = Optional.ofNullable(queryFactory
			.selectFrom(introduction)
			.join(introduction.member).on(introduction.member.id.eq(member.id), introduction.deleted.isFalse())
			.where(isMainIntroduction())
			.fetchFirst());
		List<PreferGameMode> preferGameModes = queryFactory
			.selectFrom(preferGameMode)
			.join(preferGameMode.member).on(preferGameMode.member.id.eq(member.id), preferGameMode.deleted.isFalse())
			.fetch();

		return OtherProfileServiceResponse.builder()
			.schedules(schedules.stream()
				.map(ScheduleEntry::from)
				.toList())
			.mainIntroduction(mainIntroduction.map(Introduction::getContent).orElse(null))
			.preferGameModes(preferGameModes.stream()
				.map(PreferGameModeEntry::from)
				.toList())
			.build();
	}

	public Long findUpCountByPuuid(String puuid) {
		return queryFactory.select(member.upCount)
			.from(member)
			.join(riotAccount).on(memberEq(), riotAccount.deleted.isFalse())
			.where(puuidEq(puuid))
			.fetchFirst();
	}

	private BooleanExpression puuidEq(String puuid) {
		return riotAccount.puuid.eq(puuid);
	}

	private BooleanExpression memberEq() {
		return riotAccount.member.id.eq(member.id);
	}

	private BooleanExpression isMainIntroduction() {
		return introduction.isMain.eq(true);
	}
}
