package com.gnimty.communityapiserver.domain.chat.service.dto;

import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserWithBlockDto {
    private User user;
    private Blocked status;
}
