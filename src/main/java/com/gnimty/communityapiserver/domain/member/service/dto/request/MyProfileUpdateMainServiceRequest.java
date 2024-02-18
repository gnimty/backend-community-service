package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.global.constant.Status;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MyProfileUpdateMainServiceRequest {

    private Long mainRiotAccountId;
    private Status status;
    private List<IntroductionEntry> introductions;
}
