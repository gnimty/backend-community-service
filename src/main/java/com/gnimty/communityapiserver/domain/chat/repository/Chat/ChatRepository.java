package com.gnimty.communityapiserver.domain.chat.repository.Chat;


import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String>, ChatRepositoryCustom {

    Chat save(Chat chat);


    List<ChatDto> findByChatRoomNo(Long chatRoomNo, Class<ChatDto> type);

    List<Chat> findBySenderIdAndChatRoomNo(Long senderId, Long chatRoomNo);

    void deleteByChatRoomNo(Long chatRoomNo);


}
