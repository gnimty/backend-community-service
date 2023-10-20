package com.gnimty.communityapiserver.domain.riotaccount.service;

import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateEntry;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SummonerUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountJdbcRepository;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecommendedSummonersServiceResponse;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleReadService;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.GameMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RiotAccountService {

	private final RiotAccountReadService riotAccountReadService;
	private final RiotAccountJdbcRepository riotAccountJdbcRepository;
	private final ScheduleReadService scheduleReadService;

	public void updateSummoners(SummonerUpdateServiceRequest request) {
		List<SummonerUpdateEntry> summonerUpdateEntries = request.getSummonerUpdates()
			.stream()
			.filter(v -> riotAccountReadService.findByPuuid(v.getPuuid()) != null)
			.toList();
		List<RiotAccount> existRiotAccounts = riotAccountReadService.findByPuuids(
			summonerUpdateEntries.stream().map(SummonerUpdateEntry::getPuuid).toList());
		riotAccountJdbcRepository.processBatchUpdate(existRiotAccounts, summonerUpdateEntries);
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
}
