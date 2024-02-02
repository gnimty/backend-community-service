package com.gnimty.communityapiserver.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@SpringBootTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
@ActiveProfiles("test")
class ChatRoomServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomService chatRoomService;

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
        chatRoomRepository.deleteAll();
    }

    @DisplayName("User로 채팅방 조회")
    @Nested
    class getChatRoomsByUser {

        @DisplayName("user가 포함된 채팅방이 있을 경우 성공")
        @Test
        void successToGetChatRoomListIncludingUser() {
            // given
            User user = createUser("uni", 1L);
            userRepository.save(user);

            for (Integer i = 0; i < 5; i++) {
                chatRoomRepository.save(ChatRoom.builder()
                        .chatRoomNo(i.longValue())
                        .lastModifiedDate(new Date())
                        .participants(Arrays.asList(new Participant(user, new Date(), Blocked.UNBLOCK)))
                        .createdDate(new Date())
                        .build());
            }

            // when
            List<ChatRoom> chatRoom = chatRoomService.findChatRoom(user);

            //then
            assertThat(chatRoom.size()).isEqualTo(5);
        }

        @DisplayName("user가 포함된 채팅방이 없을 경우 빈 List 반환")
        @Test
        void successToGetEmptyChatRoomList() {
            // given
            User user = createUser("uni", 1L);
            userRepository.save(user);

            // when
            List<ChatRoom> chatRoom = chatRoomService.findChatRoom(user);

            //then
            assertThat(chatRoom).isEmpty();
        }
    }


    @DisplayName("chatRoomNo로 채팅방 조회")
    @Nested
    class getChatRoomsByChatRoomNo {

        @DisplayName("채팅방이 존재할 경우 성공")
        @Test
        void successGetChatRoomByChatRoomNo() {
            // given
            User user = createUser("uni", 1L);
            userRepository.save(user);

            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomNo(1L)
                    .lastModifiedDate(new Date())
                    .participants(Arrays.asList(new Participant(user, new Date(), Blocked.UNBLOCK)))
                    .createdDate(new Date())
                    .build();
            chatRoomRepository.save(chatRoom);

            // when
            ChatRoom findChatRoom = chatRoomService.getChatRoom(1L);

            // then
            assertThat(findChatRoom).isEqualTo(chatRoom);
        }


        @DisplayName("채팅방이 존재 하지 않을 경우 실패")
        @Test
        void failGetChatRoomByChatRoomNo() {
            // given
            User user = createUser("uni", 1L);
            userRepository.save(user);

            // when & then
            assertThatThrownBy(() -> chatRoomService.getChatRoom(1L))
                    .isInstanceOf(BaseException.class)
                    .satisfies(exception -> {
                        assertThat(((BaseException) exception).getErrorCode()).isEqualTo(
                                ErrorCode.NOT_FOUND_CHAT_ROOM);
                    })
                    .hasMessageContaining(ErrorMessage.NOT_FOUND_CHAT_ROOM);
        }
    }

    @DisplayName("채팅방 사용자들로부터 ChatRoom 조회")
    @Nested
    class getChatRoomsByParticipant {

        @DisplayName("채팅방이 존재할 경우 성공")
        @Test
        void successToGetChatRoom() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomNo(1L)
                    .lastModifiedDate(new Date())
                    .participants(Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                            new Participant(userB, null, Blocked.UNBLOCK)))
                    .createdDate(new Date())
                    .build();
            chatRoomRepository.save(chatRoom);

            // when
            ChatRoom findChatRoom = chatRoomService.getChatRoom(userA, userB);

            //then
            assertThat(findChatRoom).isEqualTo(chatRoom);
        }

        @DisplayName("채팅방이 존재 하지 않을 경우 실패")
        @Test
        void failToGetChatRoom() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            // when & then
            assertThatThrownBy(() -> chatRoomService.getChatRoom(userA, userB))
                    .isInstanceOf(BaseException.class)
                    .satisfies(exception -> {
                        assertThat(((BaseException) exception).getErrorCode()).isEqualTo(
                                ErrorCode.NOT_FOUND_CHAT_ROOM);
                    })
                    .hasMessageContaining(ErrorMessage.NOT_FOUND_CHAT_ROOM_BY_USERS);
        }
    }

    @DisplayName("채팅방 사용자들로부터 Optional<ChatRoom> 조회")
    @Nested
    class findChatRoomsByParticipant {

        @DisplayName("채팅방이 존재할 경우 성공")
        @Test
        void successToGetOptionalChatRoom() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomNo(1L)
                    .lastModifiedDate(new Date())
                    .participants(Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                            new Participant(userB, null, Blocked.UNBLOCK)))
                    .createdDate(new Date())
                    .build();
            chatRoomRepository.save(chatRoom);

            // when
            Optional<ChatRoom> optionalChatRoom = chatRoomService.findChatRoom(userA, userB);

            //then
            assertThat(optionalChatRoom).isNotEmpty();
            assertThat(optionalChatRoom.get()).isEqualTo(chatRoom);
        }


        @DisplayName("채팅방이 존재 하지 않을 경우 null 반환")
        @Test
        void successToFindEmptyChatRoom() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            // when
            Optional<ChatRoom> findChatRoom = chatRoomService.findChatRoom(userA, userB);

            // then
            assertThat(findChatRoom).isEmpty();
        }
    }

    @DisplayName("채팅방 저장")
    @Nested
    class saveChatRoom {

        @DisplayName("사용자들이 포함된 채팅방이 존재하지 않을 성공")
        @Test
        void successToSaveChatRoom() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            // when
            chatRoomService.save(new UserWithBlockDto(userA, Blocked.UNBLOCK),
                    new UserWithBlockDto(userB, Blocked.BLOCK));

            //then
            assertThat(chatRoomRepository.findByUsers(userA, userB)).isNotEmpty();
        }

        @DisplayName("사용자들이 포함된 채팅방이 존재할 경우 실패")
        @Test
        void failToSaveChatRoom() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomNo(1L)
                    .lastModifiedDate(new Date())
                    .participants(Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                            new Participant(userB, null, Blocked.UNBLOCK)))
                    .createdDate(new Date())
                    .build();
            chatRoomRepository.save(chatRoom);

            // when & then
            assertThatThrownBy(
                    () -> chatRoomService.save(new UserWithBlockDto(userA, Blocked.UNBLOCK),
                            new UserWithBlockDto(userB, Blocked.BLOCK)))
                    .isInstanceOf(BaseException.class)
                    .satisfies(exception -> {
                        assertThat(((BaseException) exception).getErrorCode()).isEqualTo(
                                ErrorCode.CHATROOM_ALREADY_EXISTS);
                    })
                    .hasMessageContaining(ErrorMessage.CHATROOM_ALREADY_EXISTS);
        }
    }

    @DisplayName("채팅방 수정")
    @Nested
    class updateChatRoom {

        @DisplayName("채팅방의 마지막 수정 일자 수정 성공")
        @Test
        void successToUpdateLastModifiedDate() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomNo(1L)
                    .lastModifiedDate(new Date())
                    .participants(Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                            new Participant(userB, null, Blocked.UNBLOCK)))
                    .createdDate(new Date())
                    .build();
            chatRoomRepository.save(chatRoom);

            // when
            Date now = new Date();
            chatRoom.refreshModifiedDate(now);
            chatRoomService.update(chatRoom);

            //then
            ChatRoom findChatRoom = chatRoomRepository.findByChatRoomNo(1L).get();
            assertThat(findChatRoom).isEqualTo(chatRoom);
        }

        @DisplayName("채팅방의 차단정보 수정 성공")
        @Test
        void successToUpdateBlockInfo() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomNo(1L)
                    .lastModifiedDate(new Date())
                    .participants(Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                            new Participant(userB, null, Blocked.UNBLOCK)))
                    .createdDate(new Date())
                    .build();
            chatRoomRepository.save(chatRoom);

            // when
            chatRoom.updateParticipants(
                    Arrays.asList(new Participant(userA, new Date(), Blocked.UNBLOCK),
                            new Participant(userB, new Date(), Blocked.BLOCK)));
            chatRoomService.update(chatRoom);

            //then
            ChatRoom findChatRoom = chatRoomRepository.findByChatRoomNo(1L).get();
            assertThat(findChatRoom).isEqualTo(chatRoom);
        }
    }

    @DisplayName("채팅방 삭제")
    @Nested
    class deleteChatRoom {

        @DisplayName("채팅방 삭제 성공")
        @Test
        void successToDeleteChatRoom() {
            // given
            User user = createUser("uni", 1L);
            userRepository.save(user);

            ChatRoom chatRoom = ChatRoom.builder()
                    .chatRoomNo(1L)
                    .lastModifiedDate(new Date())
                    .participants(Arrays.asList(new Participant(user, null, Blocked.UNBLOCK)))
                    .createdDate(new Date())
                    .build();
            chatRoomRepository.save(chatRoom);

            // when
            chatRoomService.delete(chatRoom);

            //then
            assertThat(chatRoomRepository.findByChatRoomNo(1L)).isEmpty();
        }
    }

    public User createUser(String name, Long actualUserId) {
        return User.builder()
                .actualUserId(actualUserId)
                .tier(Tier.gold)
                .division(3)
                .name(name)
                .tagLine("tagLine")
                .nowStatus(Status.ONLINE).lp(3L)
                .build();
    }

}