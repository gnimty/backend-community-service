package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.SummonerUpdateServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummonerUpdateRequest {

    @Valid
    private List<SummonerUpdateEntry> summonerUpdates;

    public SummonerUpdateServiceRequest toServiceRequest() {
        return SummonerUpdateServiceRequest.builder()
            .summonerUpdates(summonerUpdates)
            .build();
    }
}
