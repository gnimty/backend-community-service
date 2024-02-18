package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RecommendedSummonersEntry {

	@Schema(example = "1", description = "소환사 id")
	private Long id;
	@Schema(example = "name", description = "소환사 name")
	private String name;
	@Schema(example = "tag", description = "소환사 tag")
	private String tagLine;
	@Schema(example = "OFFLINE", description = "소환사 상태")
	private Status status;
	@Schema(example = "true", description = "메인 소환사 계정 여부")
	private Boolean isMain;
	@Schema(example = "puuid", description = "소환사 puuid")
	private String puuid;
	@Schema(example = "DIAMOND", description = "소환사 티어")
	private Tier queue;
	@Schema(example = "100", description = "소환사 lp")
	private Long lp;
	@Schema(example = "1", description = "소환사 세부 티어")
	private Integer division;
	@Schema(example = "1000", description = "소환사 mmr")
	private Long mmr;
	@Schema(example = "TOP", description = "소환사 자주 가는 라인 1")
	private Lane frequentLane1;
	@Schema(example = "MIDDLE", description = "소환사 자주 가는 라인 2")
	private Lane frequentLane2;
	@Schema(example = "1", description = "소환사 자주 플레이하는 챔피언 id 1")
	private Long frequentChampionId1;
	@Schema(example = "2", description = "소환사 자주 플레이하는 챔피언 id 2")
	private Long frequentChampionId2;
	@Schema(example = "3", description = "소환사 자주 플레이하는 챔피언 id 3")
	private Long frequentChampionId3;
	@Schema(example = "intro", description = "소환사 소개글")
	private String introduction;
	@Schema(example = "100", description = "소환사 up count")
	private Long upCount;
	@Schema(example = "1", description = "소환사 아이콘 id")
	private Long iconId;
}
