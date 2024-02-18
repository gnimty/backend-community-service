package com.gnimty.communityapiserver.domain.riotaccount.service.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecentlySummonersEntry;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecentlySummonersServiceResponse {

	private List<RecentlySummonersEntry> recentlySummoners;
	private List<RecentlySummonersEntry> recentlySummonersFlex;
}
