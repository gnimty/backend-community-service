package com.gnimty.communityapiserver.domain.riotaccount.service.dto.request;

import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.SortBy;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecommendedSummonersServiceRequest {

	private GameMode gameMode;
	private Tier tier;
	private Status status;
	private List<Lane> lanes;
	private List<Long> preferChampionIds;
	private Boolean duoable;
	private Boolean timeMatch;
	private SortBy sortBy;
	private Long cursorId;
	private Integer pageSize;
}
