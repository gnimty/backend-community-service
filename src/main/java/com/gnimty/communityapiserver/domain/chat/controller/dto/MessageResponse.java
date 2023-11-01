package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.global.constant.MessageType;
import lombok.Data;

@Data
public class MessageResponse {

    private MessageType type;
    private Object data;

    public MessageResponse(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }
}
