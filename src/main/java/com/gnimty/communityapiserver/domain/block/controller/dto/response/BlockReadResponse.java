package com.gnimty.communityapiserver.domain.block.controller.dto.response;

import com.gnimty.communityapiserver.domain.block.service.dto.response.BlockReadServiceResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BlockReadResponse {

    @Schema(description = "차단 정보")
    private final List<BlockEntry> blocks;

    public static BlockReadResponse from(BlockReadServiceResponse response) {
        return BlockReadResponse.builder()
            .blocks(response.getBlocks())
            .build();
    }
}
