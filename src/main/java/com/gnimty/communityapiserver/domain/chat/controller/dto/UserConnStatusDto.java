package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.global.constant.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserConnStatusDto {

    private Long userId;
    private Status connStatus;
}
