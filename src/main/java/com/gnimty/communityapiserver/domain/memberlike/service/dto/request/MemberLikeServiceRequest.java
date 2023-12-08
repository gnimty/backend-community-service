package com.gnimty.communityapiserver.domain.memberlike.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberLikeServiceRequest {

    private Long targetMemberId;
    private Boolean cancel;
}
