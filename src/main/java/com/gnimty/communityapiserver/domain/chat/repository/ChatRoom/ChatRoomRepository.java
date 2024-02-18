package com.gnimty.communityapiserver.domain.chat.repository.ChatRoom;

import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String>, ChatRoomRepositoryCustom {

    Optional<ChatRoom> findByChatRoomNo(Long chatRoomNo);

    ChatRoom findFirstByOrderByChatRoomNoDesc();

    void deleteByChatRoomNo(Long chatRoomNo);

    boolean existsByChatRoomNo(Long chatRoomNo);
}
