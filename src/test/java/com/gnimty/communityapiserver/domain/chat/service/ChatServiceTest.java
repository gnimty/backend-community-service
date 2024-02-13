package com.gnimty.communityapiserver.domain.chat.service;


import static org.assertj.core.api.Assertions.assertThat;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.gnimty.communityapiserver.global.utils.InstantKoreaTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest
@ActiveProfiles(value = "test")
class ChatServiceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChatRepository chatRepository;

	@Autowired
	private ChatService chatService;

	@Autowired
	private ChatRoomRepository chatRoomRepository;


	@AfterEach
	void deleteAll() {
		userRepository.deleteAll();
		chatRepository.deleteAll();
		chatRoomRepository.deleteAll();
	}

	@DisplayName("chat으로 저장")
	@Nested
	class saveByChat {

		@DisplayName("Chat을 생성 후 저장")
		@Test
		void createChat() {
			// given
            Instant now = InstantKoreaTimeUtil.getNow();
			Chat chat = Chat.builder()
				.message("hi")
				.chatRoomNo(1L)
				.senderId(1L)
				.sendDate(now)
				.build();

			// when
			chatService.save(chat);

			// then
			Optional<Chat> findChat = chatRepository.findById(chat.getId());
			assertThat(findChat).isNotEmpty();
			assertThat(findChat.get().getSenderId()).isEqualTo(1L);
			assertThat(findChat.get().getChatRoomNo()).isEqualTo(1L);
			assertThat(findChat.get().getSendDate()).isEqualTo(now);
			assertThat(findChat.get().getMessage()).isEqualTo("hi");
		}


		@DisplayName("readByAllUser()으로 readCount를 변경한 후 다시 저장")
		@Test
		void updateChat() {
			// given
            Instant now = InstantKoreaTimeUtil.getNow();
			Chat chat = Chat.builder()
				.message("hi")
				.chatRoomNo(1L)
				.senderId(1L)
				.sendDate(now)
				.build();
			chatService.save(chat);

			// when
			chat.readByAllUser();
			chatService.save(chat);

			// then
			Optional<Chat> findChat = chatRepository.findById(chat.getId());
			assertThat(findChat).isNotEmpty();
			assertThat(findChat.get().getSenderId()).isEqualTo(1L);
			assertThat(findChat.get().getChatRoomNo()).isEqualTo(1L);
			assertThat(findChat.get().getSendDate()).isEqualTo(now);
			assertThat(findChat.get().getMessage()).isEqualTo("hi");
			assertThat(findChat.get().getReadCnt()).isEqualTo(0);
		}

	}

	@DisplayName("user, chatRoom, message, sendDate로 저장")
	@Nested
	class saveByFields {

		@DisplayName("user, chatRoom, message, sendDate로 저장")
		@Test
		void saveByFields() {
			// given
			User user = User.builder()
				.actualUserId(1L)
				.tier(Tier.gold)
				.division(3)
				.name("uni")
				.tagLine("tag")
				.nowStatus(Status.ONLINE)
				.lp(3L).build();
			userRepository.save(user);

            Instant chatRoomDate = InstantKoreaTimeUtil.getNow();
			ChatRoom chatRoom = ChatRoom.builder()
				.createdDate(chatRoomDate)
				.lastModifiedDate(chatRoomDate)
				.participants(null)
				.chatRoomNo(1L)
				.build();
			chatRoomRepository.save(chatRoom);

			// when
            Instant chatDate = InstantKoreaTimeUtil.getNow();
            Chat savedChat = chatService.save(user, chatRoom, "hi", chatDate);

			// then
			Optional<Chat> findChat = chatRepository.findById(savedChat.getId());
			assertThat(findChat).isNotEmpty();
			assertThat(findChat.get().getChatRoomNo()).isEqualTo(1L);
			assertThat(findChat.get().getMessage()).isEqualTo("hi");
			assertThat(findChat.get().getReadCnt()).isEqualTo(1);
			assertThat(findChat.get().getSenderId()).isEqualTo(user.getActualUserId());
			assertThat(findChat.get().getSendDate()).isEqualTo(chatDate);
		}

	}


	@DisplayName("chat의 readCount 수정")
	@Nested
	class updateReadCount {

		@DisplayName("readCount를 1에서 0으로 수정")
		@Test
		void updateReadCountTo0() {
			// given
            Instant chatRoomDate = InstantKoreaTimeUtil.getNow();
			ChatRoom chatRoom = ChatRoom.builder()
				.createdDate(chatRoomDate)
				.lastModifiedDate(chatRoomDate)
				.participants(null)
				.chatRoomNo(1L)
				.build();
			chatRoomRepository.save(chatRoom);

            Instant chatDate = InstantKoreaTimeUtil.getNow();
			Chat chat = Chat.builder()
				.senderId(1L)
				.chatRoomNo(chatRoom.getChatRoomNo())
				.message("hi")
				.sendDate(chatDate)
				.build();
			chatRepository.save(chat);

			// when
			chat.readByAllUser();
			chatService.save(chat);

			// then
			Chat findChat = chatRepository.findById(chat.getId()).get();
			assertThat(findChat.getReadCnt()).isEqualTo(0);
		}

		@DisplayName("채팅방의 모든 채팅내역 삭제")
		@Test
		void deleteAllChatByChatRoom() {
			// given
            Instant chatRoomDate = InstantKoreaTimeUtil.getNow();
			ChatRoom chatRoom = ChatRoom.builder()
				.createdDate(chatRoomDate)
				.lastModifiedDate(chatRoomDate)
				.participants(null)
				.chatRoomNo(1L)
				.build();
			chatRoomRepository.save(chatRoom);


			for (int i = 0; i < 10; i++) {
				chatRepository.save(Chat.builder()
					.senderId(1L)
					.chatRoomNo(chatRoom.getChatRoomNo())
					.message("hi")
					.sendDate(InstantKoreaTimeUtil.getNow())
					.build());
			}

			// when
			chatService.delete(chatRoom);

			// then
			List<ChatDto> chats = chatRepository.findByChatRoom(chatRoom, null);
			assertThat(chats).isEmpty();
		}
	}
}