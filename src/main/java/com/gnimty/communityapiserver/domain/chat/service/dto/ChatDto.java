package com.gnimty.communityapiserver.domain.chat.service.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatDto {
    private Long senderId;
    private String message;
    private Date sendDate;
    private Integer readCount;
}
