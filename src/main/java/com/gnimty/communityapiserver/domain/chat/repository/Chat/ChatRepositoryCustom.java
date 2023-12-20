package com.gnimty.communityapiserver.domain.chat.repository.Chat;


import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import java.util.Date;
import java.util.List;

public interface ChatRepositoryCustom {

	void reduceReadCntToZero(ChatRoom chatRoom, User user);

	List<Chat> findByChatRoomNoAfterExitDate(ChatRoom chatRoom, Date exitDate);

}
