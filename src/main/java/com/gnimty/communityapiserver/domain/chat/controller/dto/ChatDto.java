package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {

    private Long senderId;
    private String message;
    private OffsetDateTime sendDate;
    private Integer readCnt;

    @Builder
    public ChatDto(Chat chat) {
        this.senderId = chat.getSenderId();
        this.message = chat.getMessage();
        this.sendDate = chat.getSendDate();
        this.readCnt = chat.getReadCnt();
    }
}
