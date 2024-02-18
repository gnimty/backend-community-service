package com.gnimty.communityapiserver.domain.chat.entity;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@EqualsAndHashCode
public class ChatRoom {

    // AutoIncrementSequence에서 ChatRoom을 위한 독립적인 SEQUENCE SCOPE로 작동
    @Transient
    public static final String SEQUENCE_NAME = "chatroom_sequence";

    @Id
    private String id;

    @Indexed(unique = true)
    private Long chatRoomNo;
    private List<Participant> participants;
    private OffsetDateTime createdDate;
    private OffsetDateTime lastModifiedDate;

    @Builder
    public ChatRoom(Long chatRoomNo, List<Participant> participants) {
        this.chatRoomNo = chatRoomNo;
        this.participants = participants;

        OffsetDateTime now = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        this.createdDate = now;
        this.lastModifiedDate = now;
    }

    public void refreshModifiedDate(OffsetDateTime date) {
        this.lastModifiedDate = date.truncatedTo(ChronoUnit.MILLIS);
    }

    public void updateParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class Participant {

        @DBRef
        private User user;
        private OffsetDateTime exitDate;
        private Blocked blockedStatus;

        @Builder
        public Participant(User user, Blocked blockedStatus) {
            this.user = user;
            this.blockedStatus = blockedStatus;
            this.exitDate = null;
        }

        public void outChatRoom() {
            this.exitDate = OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        }

        public void updateBlockedStatus(Blocked blockedStatus) {
            this.blockedStatus = blockedStatus;
        }
    }
}