package com.gnimty.communityapiserver.domain.chat.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "auto_sequence")
public class AutoIncrementSequence {

    /* chatRoom의 chatRoomNo auto-increment를 구현하기 위한 collection */
    @Id
    private String id;
    private Long seq;
}