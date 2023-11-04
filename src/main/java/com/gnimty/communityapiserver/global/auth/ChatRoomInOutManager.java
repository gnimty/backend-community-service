package com.gnimty.communityapiserver.global.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChatRoomInOutManager {

    public static List<UserAndChatRoom> userAndChatRooms  = new LinkedList<>();
    public static Map<Long, List<Long>> userKey = new ConcurrentHashMap<>();


    public void access(Long userId, Long chatRoomNo) {
        userAndChatRooms.add(new UserAndChatRoom(userId, chatRoomNo));

        List<Long> chatRoomNos = userKey.get(userId);
        if (chatRoomNos == null) {
            ArrayList<Long> e = new ArrayList<>();
            e.add(chatRoomNo);
            userKey.put(userId, e);
        } else {
            log.info("chatRoomNos: {}", chatRoomNos);
            chatRoomNos.add(chatRoomNo);
        }
    }


    public void releaseChatRoom(Long userId, Long chatRoomNo) {
        UserAndChatRoom userAndChatRoom = new UserAndChatRoom(userId, chatRoomNo);
        userAndChatRooms.remove(userAndChatRoom);
    }


    public void releaseByUserId(Long userId) {
        List<Long> chatRoomNos = userKey.get(userId);
        for (Long chatRoomNo : chatRoomNos) {
            while (userAndChatRooms.remove(new UserAndChatRoom(userId, chatRoomNo))) {};
        }
    }


    @Data
    @AllArgsConstructor
    public static class UserAndChatRoom {
        private Long userId;
        private Long chatRoomNo;
    }

}