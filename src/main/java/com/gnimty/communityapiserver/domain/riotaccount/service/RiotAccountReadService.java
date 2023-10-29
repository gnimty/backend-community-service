package com.gnimty.communityapiserver.domain.riotaccount.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecommendedSummonersEntry;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountQueryRepository;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountRepository;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecommendedSummonersServiceResponse;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiotAccountReadService {

	private final RiotAccountRepository riotAccountRepository;
	private final RiotAccountQueryRepository riotAccountQueryRepository;

	public void throwIfExistsByPuuid(String puuid) {
		if (riotAccountQueryRepository.existsByPuuid(puuid)) {
			throw new BaseException(ErrorCode.ALREADY_LINKED_SUMMONER);
		}
	}

	public Boolean existsByMemberId(Member member) {
		return riotAccountQueryRepository.existsByMember(member);
	}

	public List<RiotAccount> findByMember(Member member) {
		return riotAccountRepository.findByMember(member);
	}

	public RiotAccount findMainAccountByMember(Member member) {
		return riotAccountRepository.findByMemberAndIsMain(member, true)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_LINKED_RSO));
	}

	public RiotAccount findById(Long id) {
		return riotAccountRepository.findById(id)
			.orElseThrow(() -> new BaseException(ErrorCode.RIOT_ACCOUNT_NOT_FOUND));
	}

	public List<RiotAccount> findByPuuids(List<String> puuids) {
		return riotAccountRepository.findByPuuids(puuids);
	}

	public RiotAccount findByPuuid(String puuid) {
		return riotAccountRepository.findByPuuid(puuid)
			.orElse(null);
	}

	public RecommendedSummonersServiceResponse getRecommendedSummoners(
		RecommendedSummonersServiceRequest request,
		RiotAccount mainRiotAccount,
		List<Schedule> schedules
	) {
		List<RecommendedSummonersEntry> content = riotAccountQueryRepository.findSummonersByConditions(
				Pageable.ofSize(request.getPageSize()), request, mainRiotAccount, schedules)
			.getContent();
		return RecommendedSummonersServiceResponse.builder()
			.recommendedSummoners(content)
			.build();
	}

	public RecommendedSummonersServiceResponse getMainSummoners(Member member, GameMode gameMode) {
		List<RecommendedSummonersEntry> result = riotAccountQueryRepository.findMainSummonersByMember(
			member, gameMode);
		return RecommendedSummonersServiceResponse.builder()
			.recommendedSummoners(result)
			.build();
	}
}
