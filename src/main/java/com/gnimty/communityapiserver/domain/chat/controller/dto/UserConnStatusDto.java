package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.global.constant.Status;
import lombok.Data;

@Data
public class UserConnStatusDto {

    private Long userId;
    private Status connStatus;

    public UserConnStatusDto(Long userId, Status connStatus) {
        this.userId = userId;
        this.connStatus = connStatus;
    }
}
