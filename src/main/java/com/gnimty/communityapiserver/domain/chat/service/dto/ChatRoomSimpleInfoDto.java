package com.gnimty.communityapiserver.domain.chat.service.dto;

import lombok.Data;

@Data
public class ChatRoomSimpleInfoDto {

	private Long chatRoomId;
	private Long otherUserId;
	private String lastChatMessage;
}
