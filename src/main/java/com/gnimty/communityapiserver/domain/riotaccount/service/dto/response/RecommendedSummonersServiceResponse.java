package com.gnimty.communityapiserver.domain.riotaccount.service.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecommendedSummonersEntry;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecommendedSummonersServiceResponse {

	List<RecommendedSummonersEntry> recommendedSummoners;
}
