package com.gnimty.communityapiserver.domain.block.controller.dto.response;

import com.gnimty.communityapiserver.domain.block.service.dto.response.BlockReadServiceResponse;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BlockReadResponse {

    private final List<BlockEntry> blocks;

    public static BlockReadResponse from(BlockReadServiceResponse response) {
        return BlockReadResponse.builder()
            .blocks(response.getBlocks())
            .build();
    }
}
