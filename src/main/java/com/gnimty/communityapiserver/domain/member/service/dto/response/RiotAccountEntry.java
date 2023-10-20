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
	private String summonerName;
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

	public static RiotAccountEntry from(RiotAccount riotAccount) {
		return RiotAccountEntry.builder()
			.id(riotAccount.getId())
			.summonerName(riotAccount.getSummonerName())
			.isMain(riotAccount.getIsMain())
			.puuid(riotAccount.getPuuid())
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
			.build();
	}
}
