package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.request;

import com.gnimty.communityapiserver.global.constant.Tier;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CursorEntry {

	private Long lastSummonerId;
	private String lastSummonerName;
	private Tier lastSummonerTier;
	private Integer lastSummonerDivision;
	private Long lastSummonerUpCount;
}
