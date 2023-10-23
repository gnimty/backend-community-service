package com.gnimty.communityapiserver.domain.chat.service.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatDto {
    private Long senderId;
    private String message;
    private Date sendDate;
    private Integer readCount;
}
