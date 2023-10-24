package com.gnimty.communityapiserver.domain.chat.service;


import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomInfo;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
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
@ActiveProfiles("test")
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
                users.get(0),
                users.get(i), seqGeneratorService.generateSequence(ChatRoom.SEQUENCE_NAME)));
        }

        for (Integer i = 11; i < 20; i++) {
            chatRooms.add(chatRoomRepository.save(
                users.get(10),
                users.get(i), seqGeneratorService.generateSequence(ChatRoom.SEQUENCE_NAME)));
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
        List<ChatRoomInfo> chatRoomsJoined = chatService.getChatRoomsJoined(me);

        for (ChatRoomInfo chatRoomInfo : chatRoomsJoined) {
            System.out.printf("chatRoomId = %d, otherUserId = %d\n",
                chatRoomInfo.getChatRoomId(), chatRoomInfo.getOtherUserId());
        }

        Assertions.assertEquals(9, chatRoomsJoined.size());
    }

    @Test
    void getOrCreateChatRoom_테스트() {
        List<User> all = userRepository.findAll();

        List<ChatRoom> chatRooms = chatRoomRepository.findByUser(all.get(0));
        // 이미 존재하는 유저 쌍(0, 1번 유저)은 호출 시 기존 chatRoom인 첫 번째 chatRoom과 같음
        ChatRoom chatRoom = chatService.getOrCreateChatRoom(all.get(0), all.get(1));

        Assertions.assertEquals(chatRooms.get(0).getId(), chatRoom.getId());
        // 새로운 유저 쌍 생성

        ChatRoom chatRoom2= chatService.getOrCreateChatRoom(all.get(1), all.get(2));

        Assertions.assertEquals(19, chatRoom2.getChatRoomNo());
    }

    @Test
    void exitChatRoom_테스트() {

    }

}