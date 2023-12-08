package com.gnimty.communityapiserver.domain.chat.service;


import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    public List<Chat> findChats(ChatRoom chatRoom) {
        return chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo());
    }

    public Chat save(Chat chat) {
        return chatRepository.save(chat);
    }

    public Chat save(User user, ChatRoom chatRoom, String message, Date sendDate) {
        Chat chat = Chat.builder()
            .senderId(user.getActualUserId())
            .chatRoomNo(chatRoom.getChatRoomNo())
            .message(message)
            .sendDate(sendDate)
            .build();

        return chatRepository.save(chat);
    }

    public void delete(ChatRoom chatRoom) {
        chatRepository.deleteByChatRoomNo(chatRoom.getChatRoomNo());
    }

}
