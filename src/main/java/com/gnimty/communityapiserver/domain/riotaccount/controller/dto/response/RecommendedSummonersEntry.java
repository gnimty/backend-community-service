package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import lombok.Data;

@Data
public class RecommendedSummonersEntry {

	private Long id;
	private String name;
	private String tagLine;
	private Status status;
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
	private String introduction;
	private Long upCount;
	private Long iconId;
}
