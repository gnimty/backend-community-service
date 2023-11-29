package com.gnimty.communityapiserver.domain.chat.service;


import static org.assertj.core.api.Assertions.*;

import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles(value = "local")
class ChatServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;


    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
        chatRepository.deleteAll();
    }

    @DisplayName("chat으로 저장")
    @Nested
    class saveByChat {

        @DisplayName("Chat을 생성 후 저장")
        @Test
        void createChat() {
            // given
            Date date = new Date();
            Chat chat = Chat.builder()
                .message("hi")
                .chatRoomNo(1L)
                .senderId(1L)
                .sendDate(date)
                .build();

            // when
            chatService.save(chat);

            // then
            Optional<Chat> findChat = chatRepository.findById(chat.getId());
            assertThat(findChat).isNotEmpty();
            assertThat(findChat.get().getSenderId()).isEqualTo(1L);
            assertThat(findChat.get().getChatRoomNo()).isEqualTo(1L);
            assertThat(findChat.get().getSendDate()).isEqualTo(date);
            assertThat(findChat.get().getMessage()).isEqualTo("hi");
        }

        @DisplayName("readByAllUser()으로 readCount를 변경한 후 다시 저장")
        @Test
        void updateChat() {
            // given
            Date date = new Date();
            Chat chat = Chat.builder()
                .message("hi")
                .chatRoomNo(1L)
                .senderId(1L)
                .sendDate(date)
                .build();
            chatService.save(chat);

            // when
            chat.readByAllUser();
            chatService.save(chat);

            // then
            Optional<Chat> findChat = chatRepository.findById(chat.getId());
            assertThat(findChat).isNotEmpty();
            assertThat(findChat.get().getSenderId()).isEqualTo(1L);
            assertThat(findChat.get().getChatRoomNo()).isEqualTo(1L);
            assertThat(findChat.get().getSendDate()).isEqualTo(date);
            assertThat(findChat.get().getMessage()).isEqualTo("hi");
            assertThat(findChat.get().getReadCnt()).isEqualTo(0);
        }

    }

    @DisplayName("user, chatRoom, message, sendDate로 저장")
    @Nested
    class saveByFields {

        @DisplayName("user, chatRoom, message, sendDate로 저장")
        @Test
        void saveByFields() {
            // given
            User user = User.builder()
                .actualUserId(1L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("uni")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(user);

            ChatRoom chatRoom = ChatRoom.builder()
                .createdDate(new Date())
                .lastModifiedDate(new Date())
                .participants(null)
                .chatRoomNo(1L)
                .build();
            chatRoomRepository.save(chatRoom);

            // when
            Date date = new Date();
            Chat savedChat = chatService.save(user, chatRoom, "hi", date);

            // then
            Optional<Chat> findChat = chatRepository.findById(savedChat.getId());
            assertThat(findChat).isNotEmpty();
            assertThat(findChat.get().getChatRoomNo()).isEqualTo(1L);
            assertThat(findChat.get().getMessage()).isEqualTo("hi");
            assertThat(findChat.get().getReadCnt()).isEqualTo(1);
            assertThat(findChat.get().getSenderId()).isEqualTo(user.getActualUserId());
            assertThat(findChat.get().getSendDate()).isEqualTo(date);
        }

    }

}