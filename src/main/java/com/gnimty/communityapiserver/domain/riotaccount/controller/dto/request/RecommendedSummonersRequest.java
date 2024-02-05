package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.request;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.SortBy;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import com.gnimty.communityapiserver.global.validation.annotation.ValidateCursor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ValidateCursor(sortBy = "sortBy", lastName = "lastName",
	lastSummonerMmr = "lastSummonerMmr", lastSummonerUpCount = "lastSummonerUpCount")
@AllArgsConstructor
public class RecommendedSummonersRequest {

	@Schema(example = "RANK_SOLO", description = "조회하려는 게임 모드, not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private GameMode gameMode;
	@Schema(example = "OFFLINE", description = "조회하려는 소환사 상태, not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Status status;
	@Builder.Default
	@Schema(example = "[1, 2, 3]", description = "선호 챔피언 id")
	private List<Long> preferChampionIds = new ArrayList<>();
	@Schema(example = "true", description = "듀오 가능한 소환사만 조회 여부")
	private Boolean duoable;
	@Schema(example = "DIAMOND", description = "해당 티어 이상으로 조회")
	private Tier tier;
	@Builder.Default
	@Schema(example = "[TOP, MIDDLE]", description = "조회하려는 라인")
	private List<Lane> lanes = new ArrayList<>();
	@Schema(example = "RECOMMEND", description = "정렬 기준")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private SortBy sortBy;
	@Schema(example = "true", description = "선호 플레이 시간 겹치는 소환사만 조회 여부")
	private Boolean timeMatch;
	@Schema(example = "1", description = "최근 조회한 소환사 중, 마지막 소환사의 id. 최초 조회라면 0. not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long lastSummonerId;
	@Schema(example = "name", description = "최근 조회한 소환사 중, 마지막 소환사의 name")
	private String lastName;
	@Schema(example = "1000", description = "최근 조회한 소환사 중, 마지막 소환사의 mmr")
	private Long lastSummonerMmr;
	@Schema(example = "100", description = "최근 조회한 소환사 중, 마지막 소환사의 up count")
	private Long lastSummonerUpCount;
	@Schema(example = "10", description = "조회하려는 소환사 수, not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Integer pageSize;

	public RecommendedSummonersServiceRequest toServiceRequest() {
		return RecommendedSummonersServiceRequest.builder()
			.gameMode(gameMode)
			.status(status)
			.preferChampionIds(preferChampionIds)
			.duoable(duoable)
			.tier(tier)
			.lanes(lanes)
			.sortBy(sortBy)
			.timeMatch(timeMatch)
			.lastSummonerId(lastSummonerId)
			.lastName(lastName == null ? "A" : lastName)
			.lastSummonerMmr(lastSummonerMmr == null ? 0L : lastSummonerMmr)
			.lastSummonerUpCount(lastSummonerUpCount == null ? 0L : lastSummonerUpCount)
			.pageSize(pageSize)
			.build();
	}
}
