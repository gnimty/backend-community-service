package com.gnimty.communityapiserver.global.connect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OnlineInChatRoomManager {

    @Getter
    private List<UserCurrentlyInChatRoom> userCurrentlyInChatRooms = new LinkedList<>();
    @Getter
    private Map<Long, List<Long>> userKeysCurrentlyWithChatRooms = new HashMap<>();


    public void access(Long userId, Long chatRoomNo) {
        userCurrentlyInChatRooms.add(new UserCurrentlyInChatRoom(userId, chatRoomNo));
        addToUserKey(userId, chatRoomNo);
    }

    public void release(Long userId, Long chatRoomNo) {
        userCurrentlyInChatRooms.remove(new UserCurrentlyInChatRoom(userId, chatRoomNo));
    }

    public void releaseByUserId(Long userId) {
        List<Long> chatRoomNos = userKeysCurrentlyWithChatRooms.get(userId);
        for (Long chatRoomNo : chatRoomNos) {
            while (userCurrentlyInChatRooms.remove(new UserCurrentlyInChatRoom(userId, chatRoomNo))) {};
        }
        userKeysCurrentlyWithChatRooms.remove(userId);
    }


    public boolean isUserInChatRoom(Long userId, Long chatRoomNo) {
        List<Long> userChatRoomIds = userKeysCurrentlyWithChatRooms.get(userId);
        if (userChatRoomIds == null) {
            return false;
        } else {
            return userChatRoomIds.contains(chatRoomNo);
        }
    }

    private void addToUserKey(Long userId, Long chatRoomNo) {
        List<Long> chatRoomNos = userKeysCurrentlyWithChatRooms.get(userId);
        if (chatRoomNos == null) {
            chatRoomNos = new ArrayList<>();
            chatRoomNos.add(chatRoomNo);
            userKeysCurrentlyWithChatRooms.put(userId, chatRoomNos);
        } else {
            chatRoomNos.add(chatRoomNo);
        }
    }

    @Data
    @AllArgsConstructor
    public class UserCurrentlyInChatRoom {
        private Long userId;
        private Long chatRoomNo;
    }

}