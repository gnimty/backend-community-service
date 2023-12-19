package com.gnimty.communityapiserver.domain.chat.repository.Chat;


import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;

public interface ChatRepositoryCustom {

	void reduceReadCntToZero(ChatRoom chatRoom, User user);

}
