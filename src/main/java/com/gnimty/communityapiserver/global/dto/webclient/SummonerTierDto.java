package com.gnimty.communityapiserver.global.dto.webclient;

import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.List;
import lombok.Getter;

@Getter
public class SummonerTierDto {
	private Tier tier;
	private Integer division;
	private Long lp;
	private Long mmr;
	private List<Long> mostChampionIds;
	private List<Lane> mostLanes;
}
