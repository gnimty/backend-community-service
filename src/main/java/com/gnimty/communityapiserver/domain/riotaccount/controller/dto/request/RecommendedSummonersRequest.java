package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.request;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.SortBy;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import com.gnimty.communityapiserver.global.validation.annotation.ValidateCursor;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ValidateCursor(sortBy = "sortBy", lastName = "lastName",
    lastSummonerMmr = "lastSummonerMmr", lastSummonerUpCount = "lastSummonerUpCount")
public class RecommendedSummonersRequest {

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private GameMode gameMode;
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Status status;
    private List<Long> preferChampionIds;
    private Boolean duoable;
    private Tier tier;
    private List<Lane> lanes;

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private SortBy sortBy;
    private Boolean timeMatch;
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Long lastSummonerId;
    private String lastName;
    private Long lastSummonerMmr;
    private Long lastSummonerUpCount;
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Integer pageSize;

    public RecommendedSummonersServiceRequest toServiceRequest() {
        return RecommendedSummonersServiceRequest.builder()
            .gameMode(gameMode)
            .status(status)
            .preferChampionIds(preferChampionIds)
            .duoable(duoable)
            .tier(tier)
            .lanes(lanes)
            .sortBy(sortBy)
            .timeMatch(timeMatch)
            .lastSummonerId(lastSummonerId)
            .lastName(lastName)
            .lastSummonerMmr(lastSummonerMmr)
            .lastSummonerUpCount(lastSummonerUpCount)
            .pageSize(pageSize)
            .build();
    }
}
