package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.SummonerUpdateServiceRequest;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
