package com.gnimty.communityapiserver.domain.block.controller.dto.response;

import com.gnimty.communityapiserver.domain.block.entity.Block;
import com.gnimty.communityapiserver.global.constant.Status;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BlockEntry {

	private Long id;
	private Long blockedId;
	private LocalDate date;
	private String nickname;
	private Status status;
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
