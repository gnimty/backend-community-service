package com.gnimty.communityapiserver.global.validation.validator;

import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageRequest;
import com.gnimty.communityapiserver.global.constant.MessageRequestType;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.validation.annotation.NotNullIfChatType;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatMessageValidator implements ConstraintValidator<NotNullIfChatType, MessageRequest> {


    @Override
    public void initialize(NotNullIfChatType constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MessageRequest messageRequest, ConstraintValidatorContext context) {
        if (messageRequest.getType() == MessageRequestType.CHAT) {
            throw new BaseException(ErrorCode.INVALID_CHAT_DATE);
        }
        return true;
    }
}
