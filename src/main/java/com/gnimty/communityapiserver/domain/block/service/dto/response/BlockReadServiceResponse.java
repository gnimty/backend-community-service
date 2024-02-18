package com.gnimty.communityapiserver.domain.block.service.dto.response;

import com.gnimty.communityapiserver.domain.block.controller.dto.response.BlockEntry;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BlockReadServiceResponse {

    private List<BlockEntry> blocks;
}
