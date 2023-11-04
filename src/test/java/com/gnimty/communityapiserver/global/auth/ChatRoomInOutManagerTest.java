package com.gnimty.communityapiserver.global.auth;

import com.gnimty.communityapiserver.global.auth.ChatRoomInOutManager.UserAndChatRoom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ChatRoomInOutManagerTest {

    @Autowired
    private ChatRoomInOutManager manager;

    @Test
    void release_테스트() {
        // given
        Long userId1 = 1L;

        Long chatRoomIdA = 11L;
        Long chatRoomIdB = 22L;

        manager.access(userId1, chatRoomIdA);
        manager.access(userId1, chatRoomIdA);
        manager.access(userId1, chatRoomIdB);

        // when
        manager.releaseChatRoom(userId1, chatRoomIdA);

        // then
        List<UserAndChatRoom> userAndChatRooms = ChatRoomInOutManager.userAndChatRooms;
        Assertions.assertThat(userAndChatRooms.size()).isEqualTo(2);

        // when2
        manager.releaseByUserId(userId1);

        // then2
        List<UserAndChatRoom> userAndChatRooms2 = ChatRoomInOutManager.userAndChatRooms;
        Assertions.assertThat(userAndChatRooms2.size()).isEqualTo(0);
    }

    @Test
    void removeAllTest1() {
        List<UserAndChatRoom> list  = new LinkedList<>();
        UserAndChatRoom userAndChatRoom1 = new UserAndChatRoom(1L, 2L);
        UserAndChatRoom userAndChatRoom2 = new UserAndChatRoom(1L, 2L);
        UserAndChatRoom userAndChatRoom3 = new UserAndChatRoom(2L, 3L);

        list.add(userAndChatRoom1);
        list.add(userAndChatRoom2);
        list.add(userAndChatRoom3);

        List<UserAndChatRoom> objects = new ArrayList<>();
        objects.add(userAndChatRoom1);
        objects.add(userAndChatRoom3);

        list.removeAll(objects);

        Assertions.assertThat(list.size()).isEqualTo(0);
    }

}