package com.gnimty.communityapiserver.domain.chat.repository.Chat;

import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.mongodb.client.result.UpdateResult;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
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

		UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Chat.class);
	}

	@Override
	public List<Chat> findByChatRoomNoAfterExitDate(ChatRoom chatRoom, Date exitDate) {
		Query query = new Query()
			.addCriteria(new Criteria().andOperator(
				Criteria.where("chatRoomNo").is(chatRoom.getChatRoomNo()),
				Criteria.where("sendDate").gte(exitDate)
			));
		return mongoTemplate.find(query, Chat.class);
	}
}
