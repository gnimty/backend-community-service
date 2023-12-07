package com.gnimty.communityapiserver.domain.chat.service;


import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageRequest;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.global.constant.MessageRequestType;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class OldStompServiceTest {

	@Autowired
	private SeqGeneratorService seqGeneratorService;
	@Autowired
	private StompService stompService;
	@Autowired
	private UserService userService;
	@Autowired
	private ChatRoomService chatRoomService;
	@Autowired
	private ChatService chatService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private ChatRepository chatRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChatRoomRepository chatRoomRepository;

	void clear() {
		mongoTemplate.remove(new Query(), "chatRoom");
		mongoTemplate.remove(new Query(), "user");
		mongoTemplate.remove(new Query(), "chat");
		mongoTemplate.remove(new Query(), "auto_sequence");
	}

	void 채팅방_여러개_만들기() {
		clear();
		List<User> users = new ArrayList<>();
		List<ChatRoom> chatRooms = new ArrayList<>();
//
		for (Integer i = 0; i < 20; i++) {
			users.add(userService.save(
				User.builder()
					.actualUserId(i.longValue())
					.tier(Tier.diamond)
					.division(3)
					.name("so1omon")
					.tagLine("tag")
					.status(Status.ONLINE)
					.lp(3L).build()));
		}

		for (Integer i = 1; i < 10; i++) {
			chatRooms.add(chatRoomService.save(
				new UserWithBlockDto(users.get(0), Blocked.UNBLOCK),
				new UserWithBlockDto(users.get(i), Blocked.UNBLOCK)));
		}

		for (Integer i = 11; i < 20; i++) {
			chatRooms.add(chatRoomService.save(
				new UserWithBlockDto(users.get(10), Blocked.UNBLOCK),
				new UserWithBlockDto(users.get(i), Blocked.UNBLOCK)));
		}
	}

	@BeforeEach
	void beforeUnitTest() {
		채팅방_여러개_만들기();
	}


	@Test
	void getUser_테스트() {
		// 1L부터 20L에 해당하는 유저가 존재해야 함
		for (Integer i = 0; i < 20; i++) {
			User user = userService.getUser(i.longValue());

			System.out.println("user = " + user);
		}
	}

	@Test
	void getChatRoomsJoined_테스트() {
		User me = userService.getUser(10L);
		List<ChatRoomDto> chatRoomsJoined = stompService.getChatRoomsJoined(me);

		for (ChatRoomDto chatRoomDto : chatRoomsJoined) {
			System.out.printf("chatRoomId = %d, otherUserId = %d\n",
				chatRoomDto.getChatRoomNo(), chatRoomDto.getOtherUser().getUserId());
		}
		assertThat(chatRoomsJoined.size()).isEqualTo(9);
	}

	@Test
	void getOrCreateChatRoom_테스트() {
		List<User> all = userService.findAllUser();

		List<ChatRoom> chatRooms = chatRoomService.findChatRoom(all.get(0));
		// 이미 존재하는 유저 쌍(0, 1번 유저)은 호출 시 기존 chatRoom인 첫 번째 chatRoom과 같음
		ChatRoomDto chatRoomDto1 = stompService.getOrCreateChatRoomDto(
			new UserWithBlockDto(all.get(0), Blocked.UNBLOCK),
			new UserWithBlockDto(all.get(1), Blocked.UNBLOCK)
		);
		assertThat(chatRooms.get(0).getChatRoomNo()).isEqualTo(chatRoomDto1.getChatRoomNo());
		// 새로운 유저 쌍 생성

		ChatRoomDto chatRoomDto2 = stompService.getOrCreateChatRoomDto(
			new UserWithBlockDto(all.get(1), Blocked.UNBLOCK),
			new UserWithBlockDto(all.get(2), Blocked.UNBLOCK));
		assertThat(chatRoomDto2.getChatRoomNo()).isEqualTo(19);
	}

	@Test
	void exitChatRoom_테스트() {

	}


	@Test
	void isBlockParticipant_테스트() {
		// given
		//      유저 2명
		User user1 = userService.getUser(0L);
		User user2 = userService.getUser(1L);

		//      user1이 user2를 차단
		ChatRoom chatRoom = block(user1, user2);

		// when
		boolean result1 = stompService.isBlockParticipant(chatRoom, user1);
		boolean result2 = stompService.isBlockParticipant(chatRoom, user2);

		// then
		assertThat(result1).isTrue();
		assertThat(result2).isFalse();
	}

	@Test
	void saveChat_테스트() throws InterruptedException {

		// given
		User user = userService.getUser(0L);
		ChatRoom chatRoom = chatRoomService.getChatRoom(1L);
		Date originLastModifiedDate = chatRoom.getLastModifiedDate();
		MessageRequest request = MessageRequest.builder()
			.type(MessageRequestType.CHAT)
			.data("안녕하세요")
			.build();

		// when
		sleep(3000);
		stompService.sendChat(user, chatRoom, request.getData());

		// then
		List<Chat> chats = chatService.findChats(chatRoom);
		ChatRoom updatedChatRoom = chatRoomService.getChatRoom(chatRoom.getChatRoomNo());
		assertThat(chats.size()).isEqualTo(1);
		assertThat(originLastModifiedDate).isBefore(updatedChatRoom.getLastModifiedDate());
	}

	@Test
	void updateConnStatus_테스트() {
		// given
		User user = userService.getUser(0L);

		// when
		stompService.updateConnStatus(user, Status.AWAY);

		// then
		User updatedUser = userService.getUser(0L);
		assertThat(updatedUser.getStatus()).isEqualTo(Status.AWAY);
	}

	@Test
	void readChatsInChatRoom_테스트() {
		// given
		//      유저 2명
		User user1 = userService.getUser(0L);
		User user2 = userService.getUser(1L);

		//      채팅방
		ChatRoom chatRoom = chatRoomService.getChatRoom(user1, user2);

		//      user2가 읽지 않은 채팅 5개
		for (int i = 0; i < 5; i++) {
			Chat chat = new Chat(chatRoom.getChatRoomNo(), user1.getActualUserId(), "hi",
				new Date());
			chatService.save(chat);
		}

		//      user1가 읽지 않은 채팅 5개
		for (int i = 0; i < 5; i++) {
			Chat chat = new Chat(chatRoom.getChatRoomNo(), user2.getActualUserId(), "hello",
				new Date());
			chatService.save(chat);
		}

		// when
		stompService.readOtherChats(user2, chatRoom); // user2가 채팅방 읽음
		stompService.readOtherChats(user2, chatRoom); // user2가 채팅방 읽음

		// then
		List<Chat> chats = chatService.findChats(chatRoom);
		for (Chat chat : chats) {
			if (chat.getSenderId().equals(user1.getActualUserId())) {
				assertThat(chat.getReadCnt()).isEqualTo(0);
			} else {
				assertThat(chat.getReadCnt()).isEqualTo(1);
			}
		}
	}


	private ChatRoom block(User user1, User user2) {
		ChatRoom chatRoom = chatRoomService.getChatRoom(user1, user2);
		List<Participant> originParticipants = chatRoom.getParticipants();

		List<Participant> updateParticipants = new ArrayList<>();
		Participant participant1 = originParticipants.get(0); // user1
		Participant participant2 = originParticipants.get(1); // user2

		participant1.setBlockedStatus(Blocked.BLOCK); // 차단

		updateParticipants.add(participant1);
		updateParticipants.add(participant2);
		chatRoom.updateParticipants(updateParticipants);
		chatRoomService.update(chatRoom);
		return chatRoom;
	}

	@Test
	void getChatList_테스트() {
		// given
		//      유저 2명
		User user1 = userService.getUser(0L);
		User user2 = userService.getUser(1L);

		//      채팅방
		ChatRoom chatRoom = chatRoomService.getChatRoom(user1, user2);

		// when
		//      2번 채팅 -> user2가 나감 -> 3번 채팅(user1)
		for (int i = 0; i < 2; i++) {
			Chat chat = Chat.builder()
				.chatRoomNo(chatRoom.getChatRoomNo())
				.senderId(user1.getActualUserId())
				.sendDate(new Date())
				.message("hi").build();
			chatService.save(chat);
		}

		quitRoom(chatRoom);

		for (int i = 0; i < 3; i++) {
			Chat chat = Chat.builder()
				.chatRoomNo(chatRoom.getChatRoomNo())
				.senderId(user1.getActualUserId())
				.sendDate(new Date())
				.message("hi").build();
			chatService.save(chat);
		}

		// then
		List<ChatDto> chatList1 = stompService.getChatList(user2, chatRoom);
		assertThat(chatList1.size()).isEqualTo(3);
		List<ChatDto> chatList2 = stompService.getChatList(user1, chatRoom);
		assertThat(chatList2.size()).isEqualTo(5);
	}


<<<<<<< HEAD
        // then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

    @Test
    void saveChat_테스트() throws InterruptedException {

        // given
        User user = userService.getUser(0L);
        ChatRoom chatRoom = chatRoomService.getChatRoom(1L);
        Date originLastModifiedDate = chatRoom.getLastModifiedDate();
        MessageRequest request = MessageRequest.builder()
            .type(MessageRequestType.CHAT)
            .data("안녕하세요")
            .build();

        // when
        sleep(3000);
        stompService.saveChat(user, chatRoom, request.getData());


        // then
        List<Chat> chats = chatService.findChats(chatRoom);
        ChatRoom updatedChatRoom = chatRoomService.getChatRoom(chatRoom.getChatRoomNo());
        assertThat(chats.size()).isEqualTo(1);
        assertThat(originLastModifiedDate).isBefore(updatedChatRoom.getLastModifiedDate());
    }

    @Test
    void updateConnStatus_테스트() {
        // given
        User user = userService.getUser(0L);

        // when
        stompService.updateConnStatus(user, Status.AWAY);

        // then
        User updatedUser = userService.getUser(0L);
        assertThat(updatedUser.getStatus()).isEqualTo(Status.AWAY);
    }

    @Test
    void readChatsInChatRoom_테스트() {
        // given
        //      유저 2명
        User user1 = userService.getUser(0L);
        User user2 = userService.getUser(1L);

        //      채팅방
        ChatRoom chatRoom = chatRoomService.getChatRoom(user1, user2);

        //      user2가 읽지 않은 채팅 5개
        for (int i = 0; i < 5; i++) {
            Chat chat = new Chat(chatRoom.getChatRoomNo(), user1.getActualUserId(), "hi", new Date());
            chatService.save(chat);
        }

        //      user1가 읽지 않은 채팅 5개
        for (int i = 0; i < 5; i++) {
            Chat chat = new Chat(chatRoom.getChatRoomNo(), user2.getActualUserId(), "hello", new Date());
            chatService.save(chat);
        }


        // when
        stompService.readOtherChats(user2, chatRoom); // user2가 채팅방 읽음
        stompService.readOtherChats(user2, chatRoom); // user2가 채팅방 읽음


        // then
        List<Chat> chats = chatService.findChats(chatRoom);
        for (Chat chat : chats) {
            if (chat.getSenderId().equals(user1.getActualUserId())) {
                assertThat(chat.getReadCnt()).isEqualTo(0);
            } else {
                assertThat(chat.getReadCnt()).isEqualTo(1);
            }
        }
    }


    private ChatRoom block(User user1, User user2) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(user1, user2);
        List<Participant> originParticipants = chatRoom.getParticipants();

        List<Participant> updateParticipants = new ArrayList<>();
        Participant participant1 = originParticipants.get(0); // user1
        Participant participant2 = originParticipants.get(1); // user2

        participant1.setBlockedStatus(Blocked.BLOCK); // 차단

        updateParticipants.add(participant1);
        updateParticipants.add(participant2);
        chatRoom.updateParticipants(updateParticipants);
        chatRoomService.update(chatRoom);
        return chatRoom;
    }

    @Test
    void getChatList_테스트() {
        // given
        //      유저 2명
        User user1 = userService.getUser(0L);
        User user2 = userService.getUser(1L);

        //      채팅방
        ChatRoom chatRoom = chatRoomService.getChatRoom(user1, user2);

        // when
        //      2번 채팅 -> user2가 나감 -> 3번 채팅(user1)
        for (int i = 0; i < 2; i++) {
            Chat chat = Chat.builder()
                .chatRoomNo(chatRoom.getChatRoomNo())
                .senderId(user1.getActualUserId())
                .sendDate(new Date())
                .message("hi").build();
            chatService.save(chat);
        }

        quitRoom(chatRoom);

        for (int i = 0; i < 3; i++) {
            Chat chat = Chat.builder()
                .chatRoomNo(chatRoom.getChatRoomNo())
                .senderId(user1.getActualUserId())
                .sendDate(new Date())
                .message("hi").build();
            chatService.save(chat);
        }

        // then
        List<ChatDto> chatList1 = stompService.getChatList(user2, chatRoom);
        assertThat(chatList1.size()).isEqualTo(3);
        List<ChatDto> chatList2 = stompService.getChatList(user1, chatRoom);
        assertThat(chatList2.size()).isEqualTo(5);
    }


    private void quitRoom(ChatRoom chatRoom) {
        List<Participant> participants = new ArrayList<>();
        Participant participant1 = chatRoom.getParticipants().get(0); // user1
        Participant participant2 = chatRoom.getParticipants().get(1); // user2
        // 채팅방 exitDate update
        participant2.setExitDate(new Date());
        participants.add(participant1);
        participants.add(participant2);
        chatRoom.updateParticipants(participants);
        chatRoomService.update(chatRoom);
    }
=======
	private void quitRoom(ChatRoom chatRoom) {
		List<Participant> participants = new ArrayList<>();
		Participant participant1 = chatRoom.getParticipants().get(0); // user1
		Participant participant2 = chatRoom.getParticipants().get(1); // user2
		// 채팅방 exitDate update
		participant2.setExitDate(new Date());
		participants.add(participant1);
		participants.add(participant2);
		chatRoom.updateParticipants(participants);
		chatRoomService.update(chatRoom);
	}
>>>>>>> dev

}