package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import java.util.Date;
import lombok.Builder;
import lombok.Data;


@Data
public class ChatDto {

    private Long senderId;
    private String message;
    private Date sendDate;
    private Integer readCount;

    @Builder
    public ChatDto(Chat chat) {
        this.senderId = chat.getSenderId();
        this.message = chat.getMessage();
        this.sendDate = chat.getSendDate();
        this.readCount = chat.getReadCnt();
    }
}
