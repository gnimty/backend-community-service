package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.request;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.SortBy;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendedSummonersRequest {

	private GameMode gameMode;
	private Status status;
	private List<Long> preferChampionIds;
	private Boolean duoable;
	private Tier tier;
	private List<Lane> lanes;
	private SortBy sortBy;
	private Boolean timeMatch;
	private Long lastSummonerId;
	private String lastSummonerName;
	private Tier lastSummonerTier;
	private Integer lastSummonerDivision;
	private Long lastSummonerUpCount;
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
			.lastSummonerName(lastSummonerName)
			.lastSummonerTier(lastSummonerTier)
			.lastSummonerDivision(lastSummonerDivision)
			.lastSummonerUpCount(lastSummonerUpCount)
			.pageSize(pageSize)
			.build();
	}
}
