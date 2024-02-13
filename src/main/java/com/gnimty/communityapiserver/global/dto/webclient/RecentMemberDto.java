package com.gnimty.communityapiserver.global.dto.webclient;

import lombok.Getter;

@Getter
public class RecentMemberDto {

	private String puuid;
	private Integer totalPlay;
	private Integer totalWin;
	private Integer totalDefeat;
	private Double winRate;
}
