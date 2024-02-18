package com.gnimty.communityapiserver.domain.chat.repository.ChatRoom;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.gnimty.communityapiserver.domain.chat.entity.AutoIncrementSequence;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.mongodb.client.result.UpdateResult;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {

    @Autowired
    private final MongoTemplate mongoTemplate;

    private final String COL = "chatRoom";

    @Override
    public List<ChatRoom> findByUser(User user) {
        Query query = new Query(Criteria.where("participants.user").is(user));
        List<ChatRoom> chatRooms = mongoTemplate.find(query, ChatRoom.class);
        return chatRooms;
    }

    @Override
    public List<ChatRoom> findUnBlockByUser(User user) {
        Query query = new Query().addCriteria(
            Criteria.where("participants").elemMatch(
                Criteria.where("user").is(user)
                    .and("blockedStatus").is(Blocked.UNBLOCK)
            )
        );
        List<ChatRoom> chatRooms = mongoTemplate.find(query, ChatRoom.class);
        return chatRooms;
    }

    @Override
    public Optional<ChatRoom> findByUsers(User me, User other) {
        Query query = new Query()
            .addCriteria(new Criteria().andOperator(
                Criteria.where("participants").elemMatch(
                    Criteria.where("user").is(me)
                ),
                Criteria.where("participants").elemMatch(
                    Criteria.where("user").is(other)
                )
            ));

        ChatRoom chatRoom = mongoTemplate.findOne(query, ChatRoom.class);
        return Optional.ofNullable(chatRoom);
    }

    @Override
    public ChatRoom save(List<Participant> participants) {
        // 3. 저장하고 리턴
        OffsetDateTime now = OffsetDateTime.now();
        return mongoTemplate.save(ChatRoom.builder()
            .chatRoomNo(generateSequence())
            .participants(participants)
            .build());
    }

    @Override
    public UpdateResult update(ChatRoom chatRoom) {
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoom.getChatRoomNo()));
        Update update = new Update()
            .set("participants", chatRoom.getParticipants())
            .set("lastModifiedDate", chatRoom.getLastModifiedDate());

        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ChatRoom.class);

        return updateResult;
    }

    public Long generateSequence() {
        AutoIncrementSequence counter = mongoTemplate.findAndModify(
            query(where("_id").is(COL)), new Update().inc("seq", 1),
            options().returnNew(true).upsert(true),
            AutoIncrementSequence.class);

        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }

}