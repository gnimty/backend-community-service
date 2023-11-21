package com.gnimty.communityapiserver.domain.riotaccount.service;

import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateEntry;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SummonerUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.memberlike.service.MemberLikeReadService;
import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecentlySummonersEntry;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountJdbcRepository;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecentlySummonersServiceResponse;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecommendedSummonersServiceResponse;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleReadService;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.GameMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Transactional
public class RiotAccountService {

	private final RiotAccountReadService riotAccountReadService;
	private final RiotAccountJdbcRepository riotAccountJdbcRepository;
	private final ScheduleReadService scheduleReadService;
	private final MemberLikeReadService memberLikeReadService;

	public List<RiotAccount> updateSummoners(SummonerUpdateServiceRequest request) {
		List<SummonerUpdateEntry> summonerUpdateEntries = request.getSummonerUpdates()
			.stream()
			.filter(v -> riotAccountReadService.existsByPuuid(v.getPuuid()))
			.toList();
		List<RiotAccount> existRiotAccounts = riotAccountReadService.findByPuuids(
			summonerUpdateEntries.stream().map(SummonerUpdateEntry::getPuuid).toList());
		riotAccountJdbcRepository.processBatchUpdate(existRiotAccounts, summonerUpdateEntries);
		return existRiotAccounts;
	}

	public RecommendedSummonersServiceResponse getRecommendedSummoners(
		RecommendedSummonersServiceRequest request
	) {
		Member member = MemberThreadLocal.get();
		RiotAccount mainRiotAccount = riotAccountReadService.findMainAccountByMember(member);
		List<Schedule> schedules = scheduleReadService.findByMember(member);
		return riotAccountReadService.getRecommendedSummoners(request, mainRiotAccount, schedules);
	}

	public RecommendedSummonersServiceResponse getMainSummoners(GameMode gameMode) {
		Member member = MemberThreadLocal.get();
		return riotAccountReadService.getMainSummoners(member, gameMode);
	}

	public RecentlySummonersServiceResponse getRecentlySummoners(
		Member member,
		List<Long> chattedMemberIds
	) {
		RiotAccount riotAccount = riotAccountReadService.findMainAccountByMember(member);
		RecentMemberInfo recentMemberInfo = getRecentMemberInfo(riotAccount.getSummonerName());
		List<RiotAccount> chattedRiotAccounts = getChattedRiotAccounts(chattedMemberIds);

		Map<String, RiotAccount> riotAccountMap = createRiotAccountMap(member, chattedRiotAccounts);
		List<RecentlySummonersEntry> recentlySummoners = createMatchingRecentlySummoners(
			recentMemberInfo, riotAccountMap);

		return RecentlySummonersServiceResponse.builder()
			.recentlySummoners(recentlySummoners)
			.build();
	}

	private List<RiotAccount> getChattedRiotAccounts(List<Long> chattedMemberIds) {
		return chattedMemberIds.stream()
			.map(riotAccountReadService::findMainAccountByMemberId)
			.toList();
	}

	private List<RecentlySummonersEntry> createMatchingRecentlySummoners(RecentMemberInfo recentMemberInfo,
		Map<String, RiotAccount> riotAccountMap) {
		return recentMemberInfo.getRecentMembers()
			.stream()
			.filter(rm -> riotAccountMap.containsKey(rm.getPuuid()))
			.map(rm -> RecentlySummonersEntry.of(rm, riotAccountMap.get(rm.getPuuid())))
			.collect(Collectors.toList());
	}

	private RecentMemberInfo getRecentMemberInfo(String summonerName) {
		return WebClient.create("https://gnimty.kro.kr")
			.get()
			.uri("/statistics/summoners/together/" + summonerName)
			.retrieve()
			.bodyToMono(RecentMemberInfo.class)
			.block();
	}

	private Map<String, RiotAccount> createRiotAccountMap(
		Member member,
		List<RiotAccount> chattedRiotAccounts
	) {
		return chattedRiotAccounts.stream()
			.filter(riotAccount -> !memberLikeReadService
				.existsBySourceAndTarget(member, riotAccount.getMember()))
			.collect(Collectors.toMap(RiotAccount::getPuuid, ra -> ra));
	}

	@Getter
	public static class RecentMemberInfo {

		private Integer count;
		private List<RecentMemberDto> recentMembers;
	}

	@Getter
	public static class RecentMemberDto {

		private String puuid;
		private Integer totalPlay;
		private Integer totalWin;
		private Integer totalDefeat;
		private Double winRate;
	}
}
