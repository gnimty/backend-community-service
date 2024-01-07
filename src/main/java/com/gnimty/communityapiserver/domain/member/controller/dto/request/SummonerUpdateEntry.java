package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SummonerUpdateEntry {

	@Schema(example = "name", description = "태그 이전 닉네임, not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String name;
	@Schema(example = "name#tag", description = "전체 닉네임, not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String internalTagName;
	@Schema(example = "tag", description = "태그")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String tagLine;
	@Schema(example = "puuid", description = "소환사 puuid")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String puuid;
	@Schema(example = "CHALLENGER", description = "솔로랭크 티어")
	private Tier tier;
	@Schema(example = "1", description = "솔로랭크 세부 티어")
	private Integer division;
	@Schema(example = "100", description = "솔로랭크 lp")
	private Long lp;
	@Schema(example = "2000", description = "솔로랭크 mmr")
	private Long mmr;
	@Schema(example = "[TOP, BOTTOM]", description = "솔로랭크 자주 가는 라인")
	private List<Lane> mostLanes;
	@Schema(example = "[1, 2, 3]", description = "솔로랭크 자주 플레이하는 챔피언 id")
	private List<Long> mostChampionIds;
	@Schema(example = "1", description = "소환사 아이콘 id")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long iconId;
	@Schema(example = "CHALLENGER", description = "자유랭크 티어")
	private Tier tierFlex;
	@Schema(example = "100", description = "자유랭크 lp")
	private Long lpFlex;
	@Schema(example = "1", description = "자유랭크 세부 티어")
	private Integer divisionFlex;
	@Schema(example = "2000", description = "자유랭크 mmr")
	private Long mmrFlex;
	@Schema(example = "[TOP, MIDDLE]", description = "자유랭크 자주 가는 라인")
	private List<Lane> mostLanesFlex;
	@Schema(example = "[1, 2, 3]", description = "자유랭크 자주 플레이 하는 챔피언 id")
	private List<Long> mostChampionIdsFlex;
}
