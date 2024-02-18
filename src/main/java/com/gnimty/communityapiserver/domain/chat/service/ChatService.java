package com.gnimty.communityapiserver.domain.chat.service;


import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    public List<ChatDto> findChats(ChatRoom chatRoom, OffsetDateTime exitDate) {
        return chatRepository.findByChatRoom(chatRoom, exitDate);
    }

    public void readAllChat(ChatRoom chatRoom, User other) {
        chatRepository.reduceReadCntToZero(chatRoom, other);
    }

    public Chat save(Chat chat) {
        return chatRepository.save(chat);
    }

    public Chat save(User user, ChatRoom chatRoom, String message) {
        Chat chat = Chat.builder()
            .senderId(user.getActualUserId())
            .chatRoomNo(chatRoom.getChatRoomNo())
            .message(message)
            .build();

        return chatRepository.save(chat);
    }

    public void delete(ChatRoom chatRoom) {
        chatRepository.deleteByChatRoomNo(chatRoom.getChatRoomNo());
    }

}
