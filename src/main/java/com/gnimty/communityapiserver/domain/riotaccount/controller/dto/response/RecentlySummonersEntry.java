package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountService.RecentMemberDto;
import com.gnimty.communityapiserver.global.constant.Tier;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecentlySummonersEntry {

	private Long memberId;
	private Long iconId;
	private String summonerName;
	private Tier tier;
	private Integer division;
	private Long lp;
	private Integer totalPlay;
	private Integer totalWin;
	private Integer totalDefeat;
	private Double winRate;
	private Long frequentChampionId1;
	private Long frequentChampionId2;
	private Long frequentChampionId3;

	public static RecentlySummonersEntry of(
		RecentMemberDto recentMemberDto,
		RiotAccount matchingRiotAccount
	) {
		return RecentlySummonersEntry.builder()
			.memberId(matchingRiotAccount.getMember().getId())
			.iconId(matchingRiotAccount.getIconId())
			.summonerName(matchingRiotAccount.getSummonerName())
			.tier(matchingRiotAccount.getQueue())
			.division(matchingRiotAccount.getDivision())
			.lp(matchingRiotAccount.getLp())
			.totalPlay(recentMemberDto.getTotalPlay())
			.totalWin(recentMemberDto.getTotalWin())
			.totalDefeat(recentMemberDto.getTotalDefeat())
			.winRate(recentMemberDto.getWinRate())
			.frequentChampionId1(matchingRiotAccount.getFrequentChampionId1())
			.frequentChampionId2(matchingRiotAccount.getFrequentChampionId2())
			.frequentChampionId3(matchingRiotAccount.getFrequentChampionId3())
			.build();
	}
}
