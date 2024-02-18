package com.gnimty.communityapiserver.domain.riotaccount.service.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecommendedSummonersEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RecommendedSummonersServiceResponse {

    List<RecommendedSummonersEntry> recommendedSummoners;
}
