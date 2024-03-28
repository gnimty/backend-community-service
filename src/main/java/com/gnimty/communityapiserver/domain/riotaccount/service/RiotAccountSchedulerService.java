package com.gnimty.communityapiserver.domain.riotaccount.service;

import static com.gnimty.communityapiserver.domain.riotaccount.service.utils.ChampionInfoUtil.CHAMPION_IDS;
import static com.gnimty.communityapiserver.global.constant.WebClientType.GNIMTY_GET_CHAMPION_INFO;

import com.gnimty.communityapiserver.global.constant.ChampionInfo;
import com.gnimty.communityapiserver.global.dto.webclient.ChampionInfoResponse;
import com.gnimty.communityapiserver.global.utils.WebClientUtil;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RiotAccountSchedulerService {

	public static final String CHAMPION_ID_SCHEDULER_CRON_EXPRESSION = "0 15 4 * * *";
	@Value("${gnimty.base-url}")
	private String baseUrl;

	@Scheduled(cron = CHAMPION_ID_SCHEDULER_CRON_EXPRESSION)
	public void updateChampionId() {
		ChampionInfoResponse championInfoResponse = WebClientUtil.get(ChampionInfoResponse.class,
			baseUrl + GNIMTY_GET_CHAMPION_INFO.getValue(), null);
		CHAMPION_IDS = new HashSet<>(
			championInfoResponse.getData().getChampions().stream()
				.map(ChampionInfo::getChampionId)
				.toList());
	}
}
