package com.gnimty.communityapiserver.domain.memberlike.controller.dto.request;

import com.gnimty.communityapiserver.domain.memberlike.service.dto.request.MemberLikeServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MemberLikeRequest {

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Long targetMemberId;

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Boolean cancel;

    public MemberLikeServiceRequest toServiceRequest() {
        return MemberLikeServiceRequest.builder()
            .targetMemberId(targetMemberId)
            .cancel(cancel)
            .build();
    }
}
