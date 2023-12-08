package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.SummonerUpdateServiceRequest;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;

@Getter
public class SummonerUpdateRequest {

    @Valid
    private List<SummonerUpdateEntry> summonerUpdates;

    public SummonerUpdateServiceRequest toServiceRequest() {
        return SummonerUpdateServiceRequest.builder()
            .summonerUpdates(summonerUpdates)
            .build();
    }
}
