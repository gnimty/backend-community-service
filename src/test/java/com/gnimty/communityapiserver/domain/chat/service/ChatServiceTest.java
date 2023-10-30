package com.gnimty.communityapiserver.domain.chat.service;


import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
//@ActiveProfiles("test")
class ChatServiceTest {

    @Autowired
    private SeqGeneratorService seqGeneratorService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;


    void clear() {
        mongoTemplate.remove(new Query(), "chatRoom");
        mongoTemplate.remove(new Query(), "user");
        mongoTemplate.remove(new Query(), "chat");
        mongoTemplate.remove(new Query(), "auto_sequence");
    }

    void 채팅방_여러개_만들기() {
        clear();
        List<User> users = new ArrayList<>();
        List<ChatRoom> chatRooms = new ArrayList<>();
//
        for (Integer i = 0; i < 20; i++) {
            users.add(userRepository.save(
                new User(null, i.longValue(), 3L, Tier.DIAMOND, 3,
                    "so1omon", Status.ONLINE)));
        }

        for (Integer i = 1; i < 10; i++) {
            chatRooms.add(chatRoomRepository.save(
                new UserWithBlockDto(users.get(0),Blocked.UNBLOCK),
                new UserWithBlockDto(users.get(i),Blocked.UNBLOCK),
                seqGeneratorService.generateSequence(ChatRoom.SEQUENCE_NAME)));
        }

        for (Integer i = 11; i < 20; i++) {
            chatRooms.add(chatRoomRepository.save(
                new UserWithBlockDto(users.get(10),Blocked.UNBLOCK),
                new UserWithBlockDto(users.get(i),Blocked.UNBLOCK),
                seqGeneratorService.generateSequence(ChatRoom.SEQUENCE_NAME)));
        }
    }

    @BeforeEach
    void beforeUnitTest() {
        채팅방_여러개_만들기();
    }


    @Test
    void getUser_테스트() {
        // 1L부터 20L에 해당하는 유저가 존재해야 함
        for (Integer i = 0; i < 20; i++) {
            User user = chatService.getUser(i.longValue());

            System.out.println("user = " + user);
        }
    }

    @Test
    void getChatRoomsJoined_테스트() {
        User me = userRepository.findByActualUserId(10L).get();
        List<ChatRoomDto> chatRoomsJoined = chatService.getChatRoomsJoined(me);

        for (ChatRoomDto chatRoomDto : chatRoomsJoined) {
            System.out.printf("chatRoomId = %d, otherUserId = %d\n",
                chatRoomDto.getChatRoomNo(), chatRoomDto.getOtherUser().getUserId());
        }

        assertEquals(9, chatRoomsJoined.size());
    }

    @Test
    void getOrCreateChatRoom_테스트() {
        List<User> all = userRepository.findAll();

        List<ChatRoom> chatRooms = chatRoomRepository.findByUser(all.get(0));
        // 이미 존재하는 유저 쌍(0, 1번 유저)은 호출 시 기존 chatRoom인 첫 번째 chatRoom과 같음
        ChatRoom chatRoom = chatService.getOrCreateChatRoom(
            new UserWithBlockDto(all.get(0),Blocked.UNBLOCK),
            new UserWithBlockDto(all.get(1),Blocked.UNBLOCK)
        );

        assertEquals(chatRooms.get(0).getId(), chatRoom.getId());
        // 새로운 유저 쌍 생성

        ChatRoom chatRoom2= chatService.getOrCreateChatRoom(
            new UserWithBlockDto(all.get(1),Blocked.UNBLOCK),
            new UserWithBlockDto(all.get(2),Blocked.UNBLOCK));

        assertEquals(19, chatRoom2.getChatRoomNo());
    }

    @Test
    void exitChatRoom_테스트() {

    }


    @Test
    void isBlockParticipant_테스트() {
        // given
        //      유저 2명
        User user1 = userRepository.findByActualUserId(0L).get();
        User user2 = userRepository.findByActualUserId(1L).get();

        //      user1이 user2를 차단
        ChatRoom chatRoom = block(user1, user2);


        // when
        boolean result1 = chatService.isBlockParticipant(chatRoom, user1);
        boolean result2 = chatService.isBlockParticipant(chatRoom, user2);

        // then
        Assertions.assertTrue(result1);
        Assertions.assertFalse(result2);
    }

    @Test
    void saveChat_테스트() throws InterruptedException {

        // given
        User user = userRepository.findByActualUserId(0L).get();
        ChatRoom chatRoom = chatRoomRepository.findByChatRoomNo(1L).get();
        Date originLastModifiedDate = chatRoom.getLastModifiedDate();
        String message = "안녕하세요";

        // when
        sleep(3000);
        chatService.saveChat(user, chatRoom.getChatRoomNo(), message);

        // then
        List<Chat> chats = chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo());
        ChatRoom updatedChatRoom = chatRoomRepository.findByChatRoomNo(chatRoom.getChatRoomNo()).get();
        assertEquals(1, chats.size());
        assertNotEquals(originLastModifiedDate, updatedChatRoom.getLastModifiedDate());
    }

    @NotNull
    private ChatRoom block(User user1, User user2) {
        ChatRoom chatRoom = chatRoomRepository.findByUsers(user1, user2).get();
        List<Participant> originParticipants = chatRoom.getParticipants();

        List<Participant> updateParticipants = new ArrayList<>();
        Participant participant1 = originParticipants.get(0); // user1
        Participant participant2 = originParticipants.get(1); // user2

        participant1.setBlockedStatus(Blocked.BLOCK); // 차단

        updateParticipants.add(participant1);
        updateParticipants.add(participant2);
        chatRoom.setParticipants(updateParticipants);
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    @Test
    void getChatList_테스트() {
        // given
        //      유저 2명
        User user1 = userRepository.findByActualUserId(0L).get();
        User user2 = userRepository.findByActualUserId(1L).get();

        //      채팅방
        ChatRoom chatRoom = chatRoomRepository.findByUsers(user1, user2).get();

        // when
        //      2번 채팅 -> user2가 나감 -> 3번 채팅(user1)
        for (int i = 0; i < 2; i++) {
            Chat chat = Chat.builder()
                .chatRoomNo(chatRoom.getChatRoomNo())
                .readCnt(1)
                .senderId(user1.getActualUserId())
                .sendDate(new Date())
                .message("hi").build();
            chatRepository.save(chat);
        }

        quitRoom(chatRoom);

        for (int i = 0; i < 3; i++) {
            Chat chat = Chat.builder()
                .chatRoomNo(chatRoom.getChatRoomNo())
                .readCnt(1)
                .senderId(user1.getActualUserId())
                .sendDate(new Date())
                .message("hi").build();
            chatRepository.save(chat);
        }

        // then
        List<ChatDto> chatList1 = chatService.getChatList(user2, chatRoom.getChatRoomNo());
        assertEquals(3, chatList1.size());
        List<ChatDto> chatList2 = chatService.getChatList(user1, chatRoom.getChatRoomNo());
        assertEquals(5, chatList2.size());
    }


    private void quitRoom(ChatRoom chatRoom) {
        List<Participant> participants = new ArrayList<>();
        Participant participant1 = chatRoom.getParticipants().get(0); // user1
        Participant participant2 = chatRoom.getParticipants().get(1); // user2
        // 채팅방 exitDate update
        participant2.setExitDate(new Date());
        participants.add(participant1);
        participants.add(participant2);
        chatRoom.setParticipants(participants);
        chatRoomRepository.save(chatRoom);
    }

}