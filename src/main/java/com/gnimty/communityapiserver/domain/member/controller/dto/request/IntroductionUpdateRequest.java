package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.IntroductionUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
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
public class IntroductionUpdateRequest {

    @Valid
    private List<IntroductionEntry> introductions;

    public IntroductionUpdateServiceRequest toServiceRequest() {
        return IntroductionUpdateServiceRequest.builder()
            .introductions(introductions)
            .build();
    }
}
