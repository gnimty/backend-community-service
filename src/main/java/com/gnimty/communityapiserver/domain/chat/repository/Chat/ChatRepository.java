package com.gnimty.communityapiserver.domain.chat.repository.Chat;


import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<Chat, String>, ChatRepositoryCustom {

	// 채팅방 번호와 내 정보로 채팅치기
	Chat save(Chat chat);

	List<Chat> findByChatRoomNo(Long chatRoomNo);

	List<Chat> findBySenderIdAndChatRoomNo(Long senderId, Long chatRoomNo);

	//void updateReadCountById(Long id, Integer readCount);

	void deleteByChatRoomNo(Long chatRoomNo);
}
