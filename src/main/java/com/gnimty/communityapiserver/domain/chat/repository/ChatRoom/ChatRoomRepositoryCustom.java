package com.gnimty.communityapiserver.domain.chat.repository.ChatRoom;

import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepositoryCustom {

	//    ChatRoom save(ChatRoom chatRoom);
	List<ChatRoom> findByUser(User user);

	Optional<ChatRoom> findByUsers(User user1, User user2);

//    Optional<ChatRoom> findByConds(User user1, User user2, Long chaRoomNo);

	ChatRoom save(UserWithBlockDto user1, UserWithBlockDto user2, Long chatRoomNo);

	void updateExitDate(Long chatRoomNo, User me);

	void updateBlock(Long chatRoomNo, User me);
}