package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class IntroductionUpdateServiceRequest {

    private List<IntroductionEntry> introductions;
}
