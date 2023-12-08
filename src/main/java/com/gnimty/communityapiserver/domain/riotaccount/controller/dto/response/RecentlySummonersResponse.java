package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecentlySummonersServiceResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecentlySummonersResponse {

    private List<RecentlySummonersEntry> recentlySummoners;

    public static RecentlySummonersResponse from(RecentlySummonersServiceResponse response) {
        return RecentlySummonersResponse.builder()
            .recentlySummoners(response.getRecentlySummoners())
            .build();
    }

}
