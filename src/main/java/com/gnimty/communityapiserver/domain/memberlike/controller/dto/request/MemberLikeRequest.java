package com.gnimty.communityapiserver.domain.memberlike.controller.dto.request;

import com.gnimty.communityapiserver.domain.memberlike.service.dto.request.MemberLikeServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberLikeRequest {

    @Schema(example = "1", description = "대상 회원 id, not null")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Long targetMemberId;
    @Schema(example = "true", description = "좋아요 취소 여부, not null")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Boolean cancel;

    public MemberLikeServiceRequest toServiceRequest() {
        return MemberLikeServiceRequest.builder()
            .targetMemberId(targetMemberId)
            .cancel(cancel)
            .build();
    }
}
