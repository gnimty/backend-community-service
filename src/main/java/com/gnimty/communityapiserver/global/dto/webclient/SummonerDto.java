package com.gnimty.communityapiserver.global.dto.webclient;

import lombok.Getter;

@Getter
public class SummonerDto {

	private Long profileIconId;
	private Long summonerLevel;
	private SummonerTierDto soloTierInfo;
	private SummonerTierDto flexTierInfo;
}
