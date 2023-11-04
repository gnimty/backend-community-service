package com.gnimty.communityapiserver.global.auth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccessChatRoomManager {

    public static List<UserAndChatRoom> userAndChatRooms  = new LinkedList<>();
    public static Map<Long, List<Long>> userKeys = new ConcurrentHashMap<>();


    public void access(Long userId, Long chatRoomNo) {
        userAndChatRooms.add(new UserAndChatRoom(userId, chatRoomNo));
        addToUserKey(userId, chatRoomNo);
    }

    public void release(Long userId, Long chatRoomNo) {
        userAndChatRooms.remove(new UserAndChatRoom(userId, chatRoomNo));
    }

    public void releaseByUserId(Long userId) {
        List<Long> chatRoomNos = userKeys.get(userId);
        for (Long chatRoomNo : chatRoomNos) {
            while (userAndChatRooms.remove(new UserAndChatRoom(userId, chatRoomNo))) {};
        }
    }

    public boolean isUserInChatRoom(Long userId, Long chatRoomNo) {
        List<Long> userChatRoomIds = userKeys.get(userId);
        if (userChatRoomIds == null) {
            return false;
        } else {
            return userChatRoomIds.contains(chatRoomNo);
        }
    }

    private static void addToUserKey(Long userId, Long chatRoomNo) {
        List<Long> chatRoomNos = userKeys.get(userId);
        if (chatRoomNos == null) {
            ArrayList<Long> e = new ArrayList<>();
            e.add(chatRoomNo);
            userKeys.put(userId, e);
        } else {
            chatRoomNos.add(chatRoomNo);
        }
    }


    @Data
    @AllArgsConstructor
    public static class UserAndChatRoom {
        private Long userId;
        private Long chatRoomNo;
    }

}