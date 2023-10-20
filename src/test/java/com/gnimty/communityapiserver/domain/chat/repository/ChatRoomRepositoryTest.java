package com.gnimty.communityapiserver.domain.chat.repository;

import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.Status;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.global.exception.BaseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class ChatRoomRepositoryTest {

	@Autowired
	private ChatRepository chatRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChatRoomRepository chatRoomRepository;

	void clear() {
		chatRoomRepository.deleteAll();
		userRepository.deleteAll();
		chatRepository.deleteAll();
	}

	void 채팅방_여러개_만들기() {
		clear();
		List<User> users = new ArrayList<>();
		List<ChatRoom> chatRooms = new ArrayList<>();
//
		for (Integer i = 0; i < 20; i++) {
			users.add(userRepository.save(
				new User(null, i.longValue(), 3L, "diamond", 3,
					"so1omon", Status.ONLINE)));
		}

		for (Integer i = 1; i < 10; i++) {
			List<ChatRoom.Participant> participants = new ArrayList<>();
			participants.add(new ChatRoom.Participant(users.get(0), null, Blocked.UNBLOCK));
			participants.add(new ChatRoom.Participant(users.get(i), null, Blocked.UNBLOCK));
			chatRooms.add(chatRoomRepository.save(
				new ChatRoom(null, i.longValue(), participants, new Date(), new Date())));
		}

		for (Integer i = 11; i < 20; i++) {
			List<ChatRoom.Participant> participants = new ArrayList<>();
			participants.add(new ChatRoom.Participant(users.get(10), null, Blocked.UNBLOCK));
			participants.add(new ChatRoom.Participant(users.get(i), null, Blocked.UNBLOCK));
			chatRooms.add(chatRoomRepository.save(
				new ChatRoom(null, i.longValue(), participants, new Date(), new Date())));
		}
	}

	@BeforeEach
	void beforeUnitTest() {
		채팅방_여러개_만들기();
	}


	@Test
	void 유저두명_생성해서_chatRoom에_넣기_테스트() {
		User user1 = userRepository.save(
			new User(null, 130L, 3L, "diamond", 3,
				"so1omon", Status.ONLINE));
		User user2 = userRepository.save(
			new User(null, 131L, 4L, "emerald", 3,
				"solmin23", Status.ONLINE));

		ChatRoom save = chatRoomRepository.save(user1, user2);
		System.out.println("save = " + save);
	}


	@Test
	void 채팅방_조회() {
		User user = userRepository.findByActualUserId(0L).get();

		List<ChatRoom> results = chatRoomRepository.findByUser(user);

		for (ChatRoom result : results) {
			System.out.println("result = " + result);
		}

		Assertions.assertEquals(results.size(), 9);
	}

	@Test
	void 두명이_속한_채팅방_조회() {
		User user1 = userRepository.findByActualUserId(0L).get();
		User user2 = userRepository.findByActualUserId(1L).get();

		Optional<ChatRoom> result = chatRoomRepository.findByUsers(user1, user2);

		Optional<ChatRoom> expected = chatRoomRepository.findByChatRoomNo(1L);

		Assertions.assertEquals(result.get().getId(), expected.get().getId());
	}

	@Test
	void 채팅방_생성_비정상_CASE() {
		List<User> all = userRepository.findAll();
		User user1 = all.get(0);
		User user2 = all.get(1);

		Assertions.assertThrows(BaseException.class, () -> chatRoomRepository.save(user1, user2));
	}

//	@AfterEach
//	void afterEach() {
//		chatRoomRepository.deleteAll();
//		userRepository.deleteAll();
//	}
//
//	@Autowired
//	UserRepository userRepository;
//	@Autowired
//	ChatRoomRepository chatRoomRepository;
//
//	@Test
//	ChatRoom saveChatRoom() {
//
//		// given
//		User userA = new User(1234L);
//		userRepository.save(userA);
//
//		User userB = new User(5678L);
//		userRepository.save(userB);
//
//		List<ChatUser> chatUsers = new ArrayList<>();
//		ChatUser chatUserA = convertToChatUser(userA);
//		ChatUser chatUserB = convertToChatUser(userB);
//		chatUsers.add(chatUserA);
//		chatUsers.add(chatUserB);
//
//		ChatRoom chatRoom = new ChatRoom(chatUsers);
//
//		// when
//		chatRoomRepository.save(chatRoom);
//
//		// then
//		List<Long> usersId = new ArrayList<>();
//		usersId.add(userA.getUserId());
//		usersId.add(userB.getUserId());
//		Optional<ChatRoom> findChatRoom = chatRoomRepository.findByChatUsersUserIdIn(usersId);
//
//		assertThat(findChatRoom).isNotNull();
//		assertThat(findChatRoom.get().getId()).isEqualTo(chatRoom.getId());
//
//		return chatRoom;
//	}
//
//	@Test
//	@Transactional
//	void deleteChatRoom() {
//
//		// given
//		ChatRoom chatRoom = new ChatRoom();
//		ChatRoom saveChatRoom = chatRoomRepository.save(chatRoom);
//
//		Optional<ChatRoom> findChatRoom = chatRoomRepository.findById(saveChatRoom.getId());
//		assertThat(findChatRoom).isNotEmpty();
//
//		// when
//		chatRoomRepository.deleteById(findChatRoom.get().getId());
//
//		// then
//		Optional<ChatRoom> deletedChatRoom = chatRoomRepository.findById(saveChatRoom.getId());
//		assertThat(deletedChatRoom).isEmpty();
//	}
//
//	@Test
//	ChatRoom addChat() {
//
//		// given
//		ChatRoom chatRoom = saveChatRoom();
//
//		// when
//		List<ChatUser> chatUsers = chatRoom.getChatUsers();
//		for (ChatUser chatUser : chatUsers) {
//			Long userId = chatUser.getUserId();
//			chatRoom.addChat(new Chat(userId, "안녕하세요.", LocalDateTime.now()));
//		}
//		chatRoomRepository.save(chatRoom);
//
//		// then
//		ChatRoom findChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElse(null);
//		List<Chat> chats = findChatRoom.getChats();
//		assertThat(chats.size()).isEqualTo(2);
//
//		return findChatRoom;
//	}
//	private ChatUser convertToChatUser(User user) {
//		Long userId = user.getUserId();
//		return new ChatUser(userId, LocalDateTime.now());
//	}

}