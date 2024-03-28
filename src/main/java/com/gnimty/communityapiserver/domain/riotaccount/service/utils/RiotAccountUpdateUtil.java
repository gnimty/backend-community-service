package com.gnimty.communityapiserver.domain.riotaccount.service.utils;

import static com.gnimty.communityapiserver.global.constant.WebClientType.GNIMTY_GET_SUMMONER_URI;
import static com.gnimty.communityapiserver.global.constant.WebClientType.GNIMTY_POST_SUMMONER_URI;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountReadService;
import com.gnimty.communityapiserver.global.config.async.AfterRiotAccountCommitEvent;
import com.gnimty.communityapiserver.global.dto.webclient.SummonerDto;
import com.gnimty.communityapiserver.global.dto.webclient.SummonerResponse;
import com.gnimty.communityapiserver.global.utils.WebClientUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.LinkedMultiValueMap;

@Service
@RequiredArgsConstructor
public class RiotAccountUpdateUtil {

	private final RiotAccountReadService riotAccountReadService;
	@Value("${gnimty.base-url}")
	private String baseUrl;

	@Async(value = "riotAccountExecutor")
	@Transactional
	@TransactionalEventListener
	public void updateSummonerInfo(AfterRiotAccountCommitEvent event) {
		RiotAccount riotAccount = riotAccountReadService.findById(event.getId());
		WebClientUtil.post(Object.class, baseUrl + GNIMTY_POST_SUMMONER_URI.getValue(event.getInfo().getPuuid()),
			APPLICATION_JSON, new LinkedMultiValueMap<>(), null);

		Optional<SummonerResponse> response = Optional.ofNullable(WebClientUtil.get(SummonerResponse.class,
			baseUrl + GNIMTY_GET_SUMMONER_URI.getValue(event.getInfo().getGameName(), event.getInfo().getTagLine()),
			null));

		updateEntity(riotAccount, response);
	}

	private void updateEntity(RiotAccount riotAccount, Optional<SummonerResponse> response) {
		if (response.isEmpty()) {
			return;
		}

		SummonerDto data = response.get().getData();
		riotAccount.updateIconId(data.getProfileIconId());
		riotAccount.updateLevel(data.getSummonerLevel());
		updateTierInfo(riotAccount, data);
	}

	private void updateTierInfo(RiotAccount riotAccount, SummonerDto data) {
		if (data.getSoloTierInfo() != null) {
			riotAccount.updateSoloInfo(data.getSoloTierInfo().getTier(), data.getSoloTierInfo().getDivision(),
				data.getSoloTierInfo().getLp(), data.getSoloTierInfo().getMmr(),
				data.getSoloTierInfo().getMostChampionIds(), data.getSoloTierInfo().getMostLanes());
		}
		if (data.getFlexTierInfo() != null) {
			riotAccount.updateFlexInfo(data.getFlexTierInfo().getTier(), data.getFlexTierInfo().getDivision(),
				data.getFlexTierInfo().getLp(), data.getFlexTierInfo().getMmr(),
				data.getFlexTierInfo().getMostChampionIds(), data.getFlexTierInfo().getMostLanes());
		}
	}
}
