package com.gnimty.communityapiserver.domain.chat.repository.ChatRoom;

import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepositoryCustom {

	//    ChatRoom save(ChatRoom chatRoom);
	List<ChatRoom> findByUser(User user);

	Optional<ChatRoom> findByUsers(User user1, User user2);

//    Optional<ChatRoom> findByConds(User user1, User user2, Long chaRoomNo);

	ChatRoom save(User user1, User user2);

	void updateExitDate(User user);
}