package com.gnimty.communityapiserver.domain.chat.repository.ChatRoom;

import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@RequiredArgsConstructor
public class ChatRoomRepositoryImpl implements ChatRoomRepositoryCustom {
	@Autowired
	private final MongoTemplate mongoTemplate;

	@Override
	public List<ChatRoom> findByUser(User user) {
		Query query = new Query(Criteria.where("participants.user").is(user));
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
	public ChatRoom save(User user1, User user2, Long chatRoomNo) {
		// 0. participant가 둘다 속해 있는 chatRoom이 없는지 확인
		Optional<ChatRoom> bothJoined = findByUsers(user1, user2);

		if (bothJoined.isPresent()){
			throw new BaseException(ErrorCode.CHATROOM_ALREADY_EXISTS);
		}

		// 1. unique한 chatRoomNo를 찾기 -> chatRoomNo 최댓값 + 1
		// 변경 : 이미 Sequence generator를 통해서 chatRoomNo 가져옴

		// 2. chatRoom에 User Set
		List<ChatRoom.Participant> participants = new ArrayList<>();
		participants.add(new ChatRoom.Participant(user1, null, Blocked.UNBLOCK));
		participants.add(new ChatRoom.Participant(user2, null, Blocked.UNBLOCK));

		ChatRoom chatRoom = new ChatRoom(null, chatRoomNo, participants, new Date(), new Date());

		// 3. 저장하고 리턴
		return mongoTemplate.save(chatRoom);
	}

	@Override
	public void updateExitDate(Long chatRoomNo, User me){

	}

	@Override
	public void updateBlock(Long chatRoomNo, User me){

	}
}