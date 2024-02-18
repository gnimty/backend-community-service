package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecentlySummonersServiceResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecentlySummonersResponse {

    @Schema(description = "솔로랭크 최근 정보")
    private List<RecentlySummonersEntry> recentlySummoners;
    @Schema(description = "자유랭크 최근 정보")
    private List<RecentlySummonersEntry> recentlySummonersFlex;

    public static RecentlySummonersResponse from(RecentlySummonersServiceResponse response) {
        return RecentlySummonersResponse.builder()
            .recentlySummoners(response.getRecentlySummoners())
            .recentlySummonersFlex(response.getRecentlySummonersFlex())
            .build();
    }

}
