package com.gnimty.communityapiserver.domain.chat.entity;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatRoom")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ChatRoom {

    // AutoIncrementSequence에서 ChatRoom을 위한 독립적인 SEQUENCE SCOPE로 작동
    @Transient
    public static final String SEQUENCE_NAME = "chatroom_sequence";

    @Id
    private String id;

    @Indexed(unique = true)
    private Long chatRoomNo;
    private List<Participant> participants;
    private Date createdDate;

    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    @Builder
    public static class Participant {

        @DBRef
        private User user;
        private Date exitDate;
        private Blocked blockedStatus;
    }

    private Date lastModifiedDate;

    public void refreshModifiedDate(Date date) {
        this.lastModifiedDate = date;
    }

    public void updateParticipants(List<Participant> participants) {
        this.participants = participants;
    }
}