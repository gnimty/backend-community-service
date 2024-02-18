package com.gnimty.communityapiserver.domain.block.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BlockClearServiceRequest {

    private Long id;
}
