package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.global.constant.MessageRequestType;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private MessageRequestType type;

    private String data;
}
