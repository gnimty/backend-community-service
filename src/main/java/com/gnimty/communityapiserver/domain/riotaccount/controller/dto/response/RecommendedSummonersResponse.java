package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecommendedSummonersServiceResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

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
