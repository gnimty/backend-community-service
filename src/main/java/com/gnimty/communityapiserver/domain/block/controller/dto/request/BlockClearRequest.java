package com.gnimty.communityapiserver.domain.block.controller.dto.request;

import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockClearServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockClearRequest {

    @Schema(example = "1", description = "차단 해제할 \"차단 정보 id\", not null")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Long id;

    public BlockClearServiceRequest toServiceRequest() {
        return BlockClearServiceRequest.builder()
            .id(id)
            .build();
    }
}
