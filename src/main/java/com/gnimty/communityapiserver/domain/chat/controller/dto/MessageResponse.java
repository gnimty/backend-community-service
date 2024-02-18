package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.global.constant.MessageResponseType;
import lombok.Data;

@Data
public class MessageResponse {

    private MessageResponseType type;
    private Object data;

    public MessageResponse(MessageResponseType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
