package com.gnimty.communityapiserver.domain.chat.repository.ChatRoom;

import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepositoryCustom {

	//    ChatRoom save(ChatRoom chatRoom);
	List<ChatRoom> findByUser(User user);

	List<ChatRoom> findUnBlockByUser(User user);

	Optional<ChatRoom> findByUsers(User user1, User user2);

	ChatRoom save(List<Participant> participants);

	UpdateResult update(ChatRoom chatRoom);
}