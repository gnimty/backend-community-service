package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountService.RecentMemberDto;
import com.gnimty.communityapiserver.global.constant.Tier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecentlySummonersEntry {

	@Schema(example = "1", description = "회원 id")
	private Long memberId;
	@Schema(example = "1", description = "소환사 아이콘 id")
	private Long iconId;
	@Schema(example = "name", description = "소환사 name")
	private String name;
	@Schema(example = "tag", description = "소환사 tag")
	private String tagLine;
	@Schema(example = "DIAMOND", description = "소환사 티어")
	private Tier tier;
	@Schema(example = "1", description = "소환사 세부 티어")
	private Integer division;
	@Schema(example = "100", description = "소환사 lp")
	private Long lp;
	@Schema(example = "5", description = "총 플레이")
	private Integer totalPlay;
	@Schema(example = "3", description = "총 승리")
	private Integer totalWin;
	@Schema(example = "2", description = "총 패배")
	private Integer totalDefeat;
	@Schema(example = "0.6", description = "승률")
	private Double winRate;
	@Schema(example = "1", description = "자주 플레이하는 챔피언 1")
	private Long frequentChampionId1;
	@Schema(example = "2", description = "자주 플레이하는 챔피언 2")
	private Long frequentChampionId2;
	@Schema(example = "3", description = "자주 플레이하는 챔피언 3")
	private Long frequentChampionId3;

	public static RecentlySummonersEntry of(RecentMemberDto recentMemberDto, RiotAccount matchingRiotAccount) {
		return RecentlySummonersEntry.builder()
			.memberId(matchingRiotAccount.getMember().getId())
			.iconId(matchingRiotAccount.getIconId())
			.name(matchingRiotAccount.getName())
			.tagLine(matchingRiotAccount.getTagLine())
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
