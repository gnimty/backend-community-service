package com.gnimty.communityapiserver.domain.riotaccount.service.dto.request;

import com.gnimty.communityapiserver.global.constant.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RecommendedSummonersServiceRequest {

    private GameMode gameMode;
    private Tier tier;
    private Status status;
    private List<Lane> lanes;
    private List<Long> preferChampionIds;
    private Boolean duoable;
    private Boolean timeMatch;
    private SortBy sortBy;
    private Long lastSummonerId;
    private String lastName;
    private Long lastSummonerMmr;
    private Long lastSummonerUpCount;
    private Integer pageSize;
}
