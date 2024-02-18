package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecommendedSummonersServiceResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecommendedSummonersResponse {

	private List<RecommendedSummonersEntry> recommendedSummoners;

	public static RecommendedSummonersResponse from(RecommendedSummonersServiceResponse response) {
		return RecommendedSummonersResponse.builder()
			.recommendedSummoners(response.getRecommendedSummoners())
			.build();
	}
}
