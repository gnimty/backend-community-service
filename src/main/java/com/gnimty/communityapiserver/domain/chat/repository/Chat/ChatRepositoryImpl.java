package com.gnimty.communityapiserver.domain.chat.repository.Chat;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ChatRepositoryImpl implements ChatRepositoryCustom {

    @Autowired
    private final MongoTemplate mongoTemplate;

    @Override
    public void reduceReadCntToZero(ChatRoom chatRoom, User user) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatRoomNo").is(chatRoom.getChatRoomNo()));
        query.addCriteria(Criteria.where("senderId").is(user.getActualUserId()));

        Update update = new Update()
            .set("readCnt", 0);

        mongoTemplate.updateMulti(query, update, Chat.class);
    }

    @Override
    public List<ChatDto> findByChatRoom(ChatRoom chatRoom, OffsetDateTime exitDate) {
        Query query = new Query(Criteria.where("chatRoomNo").is(chatRoom.getChatRoomNo()));
        if (exitDate != null) {
            query.addCriteria(Criteria.where("sendDate").gte(exitDate));
        }
        return mongoTemplate.find(query, ChatDto.class, "chat");
    }
}
