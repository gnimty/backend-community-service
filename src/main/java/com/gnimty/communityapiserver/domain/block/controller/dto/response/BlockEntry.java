package com.gnimty.communityapiserver.domain.block.controller.dto.response;

import com.gnimty.communityapiserver.domain.block.entity.Block;
import com.gnimty.communityapiserver.global.constant.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class BlockEntry {

    @Schema(example = "1", description = "차단 정보 id")
    private Long id;
    @Schema(example = "1", description = "차단 당한 회원의 id")
    private Long blockedId;
    @Schema(example = "2012-12-12", description = "차단한 날")
    private LocalDate date;
    @Schema(example = "nick", description = "차단 당한 회원 닉네임")
    private String nickname;
    @Schema(example = "OFFLINE", description = "차단 당한 회원 상태")
    private Status status;
    @Schema(example = "트롤임", description = "차단 시 메모")
    private String memo;

    public static BlockEntry from(Block block) {
        return BlockEntry.builder()
            .id(block.getId())
            .blockedId(block.getBlocked().getId())
            .date(block.getCreatedAt().toLocalDate())
            .nickname(block.getBlocked().getNickname())
            .status(block.getBlocked().getStatus())
            .memo(block.getMemo())
            .build();
    }
}
