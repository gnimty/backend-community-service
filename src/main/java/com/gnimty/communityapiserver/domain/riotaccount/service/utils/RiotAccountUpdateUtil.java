package com.gnimty.communityapiserver.domain.riotaccount.service.utils;

import com.gnimty.communityapiserver.domain.member.service.utils.RiotOauthUtil.RiotAccountInfo;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountReadService;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class RiotAccountUpdateUtil {

    private final RiotAccountReadService riotAccountReadService;

    @Async(value = "riotAccountExecutor")
    @Transactional
    public void updateSummonerInfo(RiotAccountInfo info, Long id) {
        RiotAccount riotAccount = riotAccountReadService.findById(id);
        WebClient.create("https://gnimty.kro.kr")
            .get()
            .uri("/summoners/" + info.getPuuid())
            .retrieve();

        Optional<SummonerResponse> response = Optional.ofNullable(WebClient.create("https://gnimty.kro.kr")
            .get()
            .uri("statistics/summoners/" + info.getGameName() + "-" + info.getTagLine())
            .retrieve()
            .bodyToMono(SummonerResponse.class)
            .block());

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

    @Getter
    public static class SummonerResponse {

        private SummonerDto data;
    }

    @Getter
    public static class SummonerDto {

        private Long profileIconId;
        private Long summonerLevel;
        private SummonerTierDto soloTierInfo;
        private SummonerTierDto flexTierInfo;
    }

    @Getter
    public static class SummonerTierDto {

        private Tier tier;
        private Integer division;
        private Long lp;
        private Long mmr;
        private List<Long> mostChampionIds;
        private List<Lane> mostLanes;
    }

}
