package com.gnimty.communityapiserver.domain.chat.repository.Chat;


import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import java.util.Date;
import java.util.List;

public interface ChatRepositoryCustom {

	void reduceReadCntToZero(ChatRoom chatRoom, User user);

	List<ChatDto> findByChatRoom(ChatRoom chatRoom, Date exitDate);

}
