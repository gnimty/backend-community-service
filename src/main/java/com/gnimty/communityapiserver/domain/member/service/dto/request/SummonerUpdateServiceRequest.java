package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateEntry;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SummonerUpdateServiceRequest {

    private List<SummonerUpdateEntry> summonerUpdates;
}
