package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.global.constant.MessageRequestType;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageRequest {

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private MessageRequestType type;

    private String data;

    public MessageRequest toServiceRequest() {
        return MessageRequest.builder()
            .type(type)
            .data(data)
            .build();
    }
}
