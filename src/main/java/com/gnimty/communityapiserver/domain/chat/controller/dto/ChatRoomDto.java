package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import java.util.Date;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
public class ChatRoomDto {

    private Long chatRoomNo;
    private Date lastModified;
    private UserDto otherUser;
    private List<ChatDto> chats;

    @Builder
    public ChatRoomDto(ChatRoom chatRoom, UserDto other, List<ChatDto> chats) {
        this.chatRoomNo = chatRoom.getChatRoomNo();
        this.lastModified = chatRoom.getLastModifiedDate();
        this.otherUser = other;
        this.chats = chats;
    }
}
