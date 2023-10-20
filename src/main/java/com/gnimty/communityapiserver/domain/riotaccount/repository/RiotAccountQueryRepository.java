package com.gnimty.communityapiserver.domain.riotaccount.repository;

import static com.gnimty.communityapiserver.domain.introduction.entity.QIntroduction.introduction;
import static com.gnimty.communityapiserver.domain.member.entity.QMember.member;
import static com.gnimty.communityapiserver.domain.prefergamemode.entity.QPreferGameMode.preferGameMode;
import static com.gnimty.communityapiserver.domain.riotaccount.entity.QRiotAccount.riotAccount;
import static com.gnimty.communityapiserver.domain.schedule.entity.QSchedule.schedule;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecommendedSummonersEntry;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.SortBy;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RiotAccountQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<RecommendedSummonersEntry> findSummonersByConditions(
		Pageable pageable,
		RecommendedSummonersServiceRequest request,
		RiotAccount mainRiotAccount,
		List<Schedule> schedules
	) {
		Member me = MemberThreadLocal.get();
		JPAQuery<RecommendedSummonersEntry> query = queryFactory.select(
				getProjectionBean())
			.from(riotAccount)
			.join(riotAccount.member, member)
			.join(preferGameMode).on(preferGameMode.member.eq(member))
			.join(introduction).on(introduction.member.eq(member))
			.join(schedule).on(schedule.member.eq(member))
			.where(
				cursorIdGt(request.getCursorId())
					.and(isMainRiotAccount())
					.and(excludeMasterGoe())
					.and(gameModeEq(request.getGameMode()))
					.and(tierGoe(request.getTier()))
					.and(memberStatusEq(request.getStatus()))
					.and(laneEq(request.getLanes()))
					.and(frequentChampionIdEq(request.getPreferChampionIds()))
					.and(duoable(mainRiotAccount.getQueue(), mainRiotAccount.getDivision(),
						request.getDuoable()))
					.and(timeMatch(schedules, request.getTimeMatch()))
					.and(riotAccount.member.id.ne(me.getId())))
			.limit(pageable.getPageSize());
		if (request.getSortBy().equals(SortBy.ATOZ)) {
			query = query.orderBy(riotAccount.summonerName.asc());
		} else if (request.getSortBy().equals(SortBy.TIER)) {
			query = query.orderBy(riotAccount.queue.desc())
				.orderBy(riotAccount.division.asc())
				.orderBy(riotAccount.summonerName.asc());
		} else if (request.getSortBy().equals(SortBy.RECOMMEND)) {
			query = query.orderBy(member.upCount.desc())
				.orderBy(riotAccount.summonerName.asc());
		}
		query = query.orderBy(riotAccount.id.asc());

		List<RecommendedSummonersEntry> fetch = query.fetch();
		boolean hasNext = false;
		if (fetch.size() > pageable.getPageSize()) {
			fetch.remove(pageable.getPageSize());
			hasNext = true;
		}
		return new SliceImpl<>(fetch, pageable, hasNext);
	}

	public List<RecommendedSummonersEntry> findMainSummonersByMember(Member me, GameMode gameMode) {
		JPAQuery<RecommendedSummonersEntry> query = queryFactory.select(
				getProjectionBean())
			.from(riotAccount)
			.join(riotAccount.member, member)
			.join(preferGameMode).on(preferGameMode.member.eq(member))
			.join(introduction).on(introduction.member.eq(member))
			.join(schedule).on(schedule.member.eq(member))
			.where(
				memberStatusEq(Status.ONLINE)
					.and(gameModeEq(gameMode))
					.and(memberNotEq(me)));

		if (me == null || !existsByMember(me)) {
			return notLinkedSummonersQuery(query);
		}
		RiotAccount account = queryFactory
			.selectFrom(riotAccount)
			.where(riotAccount.member.id.eq(me.getId()))
			.fetchFirst();
		return query
			.where(duoable(account.getQueue(), account.getDivision(), true))
			.orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
			.limit(5)
			.fetch();
	}

	private Boolean existsByMember(Member member) {
		return queryFactory
			.selectOne()
			.from(riotAccount)
			.where(riotAccount.member.id.eq(member.getId()))
			.fetchFirst() != null;
	}

	private List<RecommendedSummonersEntry> notLinkedSummonersQuery(
		JPAQuery<RecommendedSummonersEntry> query) {
		return query
			.orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
			.limit(5)
			.fetch();
	}

	private BooleanExpression memberNotEq(Member me) {
		if (me == null) {
			return null;
		}
		return riotAccount.member.id.ne(me.getId());
	}

	private QBean<RecommendedSummonersEntry> getProjectionBean() {
		return Projections.bean(
			RecommendedSummonersEntry.class,
			riotAccount.id.as("id"),
			riotAccount.summonerName.as("summonerName"),
			member.status.as("status"),
			riotAccount.isMain.as("isMain"),
			riotAccount.puuid.as("puuid"),
			riotAccount.queue.as("queue"),
			riotAccount.lp.as("lp"),
			riotAccount.division.as("division"),
			riotAccount.mmr.as("mmr"),
			riotAccount.frequentLane1.as("frequentLane1"),
			riotAccount.frequentLane2.as("frequentLane2"),
			riotAccount.frequentChampionId1.as("frequentChampionId1"),
			riotAccount.frequentChampionId2.as("frequentChampionId2"),
			riotAccount.frequentChampionId3.as("frequentChampionId3"),
			introduction.content.as("introduction"),
			member.upCount.as("upCount"),
			riotAccount.iconId.as("iconId")
		);
	}

	private BooleanExpression isMainRiotAccount() {
		return riotAccount.isMain.isTrue();
	}

	private BooleanExpression cursorIdGt(Long cursorId) {
		return cursorId == null ? null : riotAccount.id.gt(cursorId);
	}

	private BooleanBuilder timeMatch(List<Schedule> schedules, Boolean timeMatch) {
		if (!timeMatch) {
			return null;
		}
		BooleanBuilder builder = new BooleanBuilder();

		for (Schedule inp : schedules) {
			builder.or(schedule.dayOfWeek.eq(inp.getDayOfWeek())
				.and(schedule.startTime.gt(inp.getEndTime())
					.or(schedule.endTime.lt(inp.getStartTime()))
					.not()));
		}
		return builder;
	}

	private BooleanExpression excludeMasterGoe() {
		return riotAccount.queue.loe(Tier.DIAMOND);
	}

	private BooleanExpression duoable(Tier tier, Integer division, Boolean duoable) {
		if (duoable == null || !duoable) {
			return null;
		}
		if (tier.equals(Tier.IRON) || tier.equals(Tier.BRONZE)) {
			return riotAccount.queue.loe(Tier.SILVER);
		}
		if (tier.equals(Tier.SILVER)) {
			return riotAccount.queue.loe(Tier.GOLD);
		}
		if (tier.equals(Tier.GOLD)) {
			return riotAccount.queue.loe(Tier.PLATINUM)
				.and(riotAccount.queue.goe(Tier.SILVER));
		}
		if (tier.equals(Tier.PLATINUM)) {
			return riotAccount.queue.loe(Tier.EMERALD)
				.and(riotAccount.queue.goe(Tier.GOLD));
		}
		if (tier.equals(Tier.EMERALD)) {
			BooleanExpression be = riotAccount.queue.goe(Tier.PLATINUM);
			if (division > 2) {
				return be.and(riotAccount.queue.loe(Tier.EMERALD));
			}
			if (division == 2) {
				return be
					.and(riotAccount.queue.loe(Tier.EMERALD)
						.or(riotAccount.queue.eq(Tier.DIAMOND)
							.and(riotAccount.division.eq(4))));
			}
			return be
				.and(riotAccount.queue.loe(Tier.EMERALD)
					.or(riotAccount.queue.eq(Tier.DIAMOND)
						.and(riotAccount.division.goe(3))));
		}

		// tier = DIAMOND
		if (division == 1) {
			return riotAccount.queue.eq(Tier.DIAMOND)
				.and(riotAccount.division.loe(3));
		}
		if (division == 2) {
			return riotAccount.queue.eq(Tier.DIAMOND);
		}
		if (division == 3) {
			return riotAccount.queue.eq(Tier.DIAMOND)
				.or(riotAccount.queue.eq(Tier.EMERALD).and(riotAccount.division.eq(1)));
		}

		// 다이아 4
		return riotAccount.queue
			.eq(Tier.EMERALD).and(riotAccount.division.loe(2)) // 에메랄드 1, 2와 가능하고
			.or(riotAccount.queue.eq(Tier.DIAMOND)
				.and(riotAccount.division.goe(2))); // 다이아 2, 3, 4와 가능
	}

	private BooleanExpression gameModeEq(GameMode gameMode) {
		if (gameMode == null) {
			return null;
		}
		return preferGameMode.gameMode.eq(gameMode);
	}

	private BooleanExpression memberStatusEq(Status status) {
		if (status == null) {
			return null;
		}
		return member.status.eq(status);
	}

	private BooleanExpression frequentChampionIdEq(List<Long> preferChampionIds) {
		if (preferChampionIds == null || preferChampionIds.isEmpty()) {
			return null;
		}
		return riotAccount.frequentChampionId1.in(preferChampionIds)
			.or(riotAccount.frequentChampionId2.in(preferChampionIds)
				.or(riotAccount.frequentChampionId3.in(preferChampionIds)));
	}

	private BooleanExpression tierGoe(Tier tier) {
		if (tier == null) {
			return null;
		}
		return riotAccount.queue.goe(tier);
	}

	private BooleanExpression laneEq(List<Lane> lanes) {
		if (lanes == null || lanes.isEmpty()) {
			return null;
		}
		return riotAccount.frequentLane1.in(lanes)
			.or(riotAccount.frequentLane2.in(lanes));
	}
}
