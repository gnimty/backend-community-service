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
import com.querydsl.core.types.OrderSpecifier;
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

	public Boolean existsByPuuid(String puuid) {
		return queryFactory
			.selectOne()
			.from(riotAccount)
			.where(riotAccount.puuid.eq(puuid))
			.fetchFirst() != null;
	}

	public Boolean existsByMember(Member member) {
		return queryFactory
			.selectOne()
			.from(riotAccount)
			.where(riotAccount.member.id.eq(member.getId()))
			.fetchFirst() != null;
	}

	public Slice<RecommendedSummonersEntry> findSummonersByConditions(
		Pageable pageable,
		RecommendedSummonersServiceRequest request,
		RiotAccount mainRiotAccount,
		List<Schedule> schedules
	) {
		Member me = MemberThreadLocal.get();
		OrderSpecifier<?>[] orderSpecifier = createOrderSpecifier(request.getSortBy());

		JPAQuery<RecommendedSummonersEntry> query = queryFactory.select(
				getProjectionBean())
			.from(riotAccount)
			.join(riotAccount.member, member)
			.join(preferGameMode).on(preferGameMode.member.eq(member))
			.join(introduction).on(introduction.member.eq(member))
			.join(schedule).on(schedule.member.eq(member))
			.where(
				cursorGt(request)
					.and(isMainRiotAccount())
					.and(excludeMasterGoe())
					.and(gameModeEq(request.getGameMode()))
					.and(tierGoe(request.getLastSummonerMmr()))
					.and(memberStatusEq(request.getStatus()))
					.and(laneEq(request.getLanes()))
					.and(frequentChampionIdEq(request.getPreferChampionIds()))
					.and(duoable(mainRiotAccount.getQueue(), mainRiotAccount.getDivision(),
						request.getDuoable()))
					.and(timeMatch(schedules, request.getTimeMatch()))
					.and(riotAccount.member.id.ne(me.getId())))
			.orderBy(orderSpecifier)
			.limit(pageable.getPageSize());

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

	private BooleanBuilder timeMatch(List<Schedule> schedules, Boolean timeMatch) {
		if (timeMatch == null || !timeMatch) {
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
		return riotAccount.mmr.lt(getMmrByTier(Tier.MASTER));
	}

	private BooleanExpression duoable(
		Tier tier,
		Integer division,
		Boolean duoable
	) {
		if (duoable == null || !duoable) {
			return null;
		}
		if (tier.equals(Tier.IRON) || tier.equals(Tier.BRONZE)) {
			return riotAccount.mmr.lt(getMmrByTier(Tier.GOLD));
		}
		if (tier.equals(Tier.SILVER)) {
			return riotAccount.mmr.lt(getMmrByTier(Tier.PLATINUM));
		}
		if (tier.equals(Tier.GOLD)) {
			return riotAccount.mmr.goe(getMmrByTier(Tier.SILVER))
				.and(riotAccount.mmr.lt(getMmrByTier(Tier.EMERALD)));
		}
		if (tier.equals(Tier.PLATINUM)) {
			return riotAccount.mmr.goe(getMmrByTier(Tier.GOLD))
				.and(riotAccount.mmr.lt(getMmrByTier(Tier.DIAMOND)));
		}
		if (tier.equals(Tier.EMERALD)) {
			BooleanExpression be = riotAccount.mmr.goe(getMmrByTier(Tier.PLATINUM));
			if (division > 2) {
				return be.and(riotAccount.mmr.lt(getMmrByTier(Tier.DIAMOND)));
			}
			if (division == 2) {
				return be
					.and(riotAccount.mmr.lt(getMmrByTierAndDivision(Tier.DIAMOND, 3)));
			}
			return be
				.and(riotAccount.mmr.lt(getMmrByTierAndDivision(Tier.DIAMOND, 2)));
		}

		// tier = DIAMOND
		return riotAccount.mmr.goe(
			getMmrByTierAndDivision(Tier.DIAMOND, 3) - (division - 1) * 100L);
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

	private BooleanExpression tierGoe(Long mmr) {
		if (mmr == null) {
			return null;
		}
		return riotAccount.mmr.goe(mmr);
	}

	private BooleanExpression laneEq(List<Lane> lanes) {
		if (lanes == null || lanes.isEmpty()) {
			return null;
		}
		return riotAccount.frequentLane1.in(lanes)
			.or(riotAccount.frequentLane2.in(lanes));
	}

	private BooleanExpression cursorGt(RecommendedSummonersServiceRequest request) {
		SortBy sortBy = request.getSortBy();
		if (sortBy == null) {
			return riotAccount.id.gt(request.getLastSummonerId());
		}
		if (sortBy.equals(SortBy.ATOZ)) {
			return riotAccount.summonerName.lower().goe(request.getLastSummonerName().toLowerCase())
				.and(
					riotAccount.summonerName.lower().gt(request.getLastSummonerName().toLowerCase())
						.or(riotAccount.id.gt(request.getLastSummonerId())));
		} else if (sortBy.equals(SortBy.TIER)) {
			return riotAccount.mmr.goe(request.getLastSummonerMmr())
				.and(riotAccount.mmr.gt(request.getLastSummonerMmr())
					.or(riotAccount.id.gt(request.getLastSummonerId())));
		}
		return member.upCount.goe(request.getLastSummonerUpCount())
			.and(member.upCount.gt(request.getLastSummonerUpCount())
				.or(riotAccount.id.gt(request.getLastSummonerId())));
	}

	private OrderSpecifier<?>[] createOrderSpecifier(SortBy sortBy) {
		OrderSpecifier<String> summonerNameAsc = riotAccount.summonerName.toLowerCase().asc();
		OrderSpecifier<Long> mmrDesc = riotAccount.mmr.desc();
		OrderSpecifier<Long> upCountDesc = member.upCount.desc();
		OrderSpecifier<Long> idAsc = riotAccount.id.asc();

		if (sortBy == null) {
			return new OrderSpecifier[]{idAsc};
		}
		if (sortBy.equals(SortBy.ATOZ)) {
			return new OrderSpecifier[]{summonerNameAsc, idAsc};
		} else if (sortBy.equals(SortBy.TIER)) {
			return new OrderSpecifier[]{mmrDesc, idAsc};
		} else {
			return new OrderSpecifier[]{upCountDesc, idAsc};
		}
	}

	/**
	 * 			IV		III		II		I
	 * 아이언		LP+0	LP+100	LP+200	LP+300
	 * 브론즈		LP+400	LP+500	LP+600	LP+700
	 * 실버		LP+800	LP+900	LP+1000	LP+1100
	 * 골드		LP+1200	LP+1300	LP+1400	LP+1500
	 * 플레티넘	LP+1600	LP+1700	LP+1800	LP+1900
	 * 에메랄드	LP+2000	LP+2100	LP+2200	LP+2300
	 * 다이아몬드	LP+2400	LP+2500	LP+2600	LP+2700
	 * 마스터 /그랜드마스터 / 챌린저	LP+2800
	 */
	private Long getMmrByTierAndDivision(Tier tier, Integer division) {
		if (tier.getWeight() >= Tier.MASTER.getWeight()) {
			return 2800L;
		}
		return tier.getWeight() * 400 + (4 - division) * 100L;
	}

	private Long getMmrByTier(Tier tier) {
		if (tier.getWeight() >= Tier.MASTER.getWeight()) {
			return 2800L;
		}
		return tier.getWeight() * 400L;
	}
}
