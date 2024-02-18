package com.gnimty.communityapiserver.domain.block.service.dto.response;

import com.gnimty.communityapiserver.domain.block.controller.dto.response.BlockEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BlockReadServiceResponse {

    private List<BlockEntry> blocks;
}
