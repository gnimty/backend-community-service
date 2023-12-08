package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Tier;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RiotAccountEntry {

	private Long id;
	private String name;
	private String tagLine;
	private Boolean isMain;
	private String puuid;
	private Tier queue;
	private Long lp;
	private Integer division;
	private Long mmr;
	private Lane frequentLane1;
	private Lane frequentLane2;
	private Long frequentChampionId1;
	private Long frequentChampionId2;
	private Long frequentChampionId3;
	private Tier queueFlex;
	private Long lpFlex;
	private Integer divisionFlex;
	private Long mmrFlex;
	private Lane frequentLane1Flex;
	private Lane frequentLane2Flex;
	private Long frequentChampionId1Flex;
	private Long frequentChampionId2Flex;
	private Long frequentChampionId3Flex;
	private Long iconId;

	public static RiotAccountEntry from(RiotAccount riotAccount) {
		return RiotAccountEntry.builder()
			.id(riotAccount.getId())
			.name(riotAccount.getName())
			.tagLine(riotAccount.getTagLine())
			.isMain(riotAccount.getIsMain())
			.puuid(riotAccount.getPuuid())
			.queue(riotAccount.getQueue())
			.lp(riotAccount.getLp())
			.division(riotAccount.getDivision())
			.mmr(riotAccount.getMmr())
			.frequentLane1(riotAccount.getFrequentLane1())
			.frequentLane2(riotAccount.getFrequentLane2())
			.frequentChampionId1(riotAccount.getFrequentChampionId1())
			.frequentChampionId2(riotAccount.getFrequentChampionId2())
			.frequentChampionId3(riotAccount.getFrequentChampionId3())
			.queueFlex(riotAccount.getQueueFlex())
			.lpFlex(riotAccount.getLpFlex())
			.divisionFlex(riotAccount.getDivisionFlex())
			.mmrFlex(riotAccount.getMmrFlex())
			.frequentLane1Flex(riotAccount.getFrequentLane1Flex())
			.frequentLane2Flex(riotAccount.getFrequentLane2Flex())
			.frequentChampionId1Flex(riotAccount.getFrequentChampionId1Flex())
			.frequentChampionId2Flex(riotAccount.getFrequentChampionId2Flex())
			.frequentChampionId3Flex(riotAccount.getFrequentChampionId3Flex())
			.iconId(riotAccount.getIconId())
			.build();
	}
}
