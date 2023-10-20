package com.gnimty.communityapiserver.domain.chat.controller.dto;

import lombok.Data;

@Data
public class ChatRoomInfo {

	private Long chatRoomId;
	private Long otherUserId;

	// ... 필드 추가 예정

	public ChatRoomInfo(Long chatRoomId, Long otherUserId) {
		this.chatRoomId = chatRoomId;
		this.otherUserId = otherUserId;
	}

	// ... 추가할 예정
}
