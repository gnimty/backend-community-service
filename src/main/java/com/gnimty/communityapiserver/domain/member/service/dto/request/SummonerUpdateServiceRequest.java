package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SummonerUpdateServiceRequest {

    private List<SummonerUpdateEntry> summonerUpdates;
}
