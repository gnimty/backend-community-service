package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.global.constant.Status;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyProfileUpdateServiceRequest {

    private Long mainRiotAccountId;
    private Status status;
    private List<IntroductionEntry> introductions;
}
