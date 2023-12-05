package com.gnimty.communityapiserver.domain.chat.service;


import static org.assertj.core.api.Assertions.*;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.auth.AuthStateCacheable;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@SpringBootTest
@ActiveProfiles(value = "local")
public class StompServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private StompService stompService;


    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
        chatRoomRepository.deleteAll();
    }


    @DisplayName("유저의 접속상태 수정 시")
    @Nested
    class updateConnectStatus {

        @DisplayName("수정 성공")
        @Test
        void successUpdateConnectStatus() {
            // given
            User user = User.builder()
                .actualUserId(1L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("uni")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(user);

            // when
            stompService.updateConnStatus(user, Status.OFFLINE);

            // then
            User findUser = userService.getUser(1L);
            assertThat(findUser.getStatus()).isEqualTo(Status.OFFLINE);
        }
    }

    @DisplayName("채팅방 생성 또는 조회")
    @Nested
    class getOrCreateChatRoom {

        private User userA;
        private User userB;

        @BeforeEach
        void saveTwoUser() {
            userA = User.builder()
                .actualUserId(1L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("uni")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(userA);

            userB = User.builder()
                .actualUserId(2L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("inu")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(userB);
        }

        @DisplayName("두 유저의 채팅방이 존재한 경우, 기존의 채팅방 조회 성공")
        @Test
        void successGetExistingChatRoom() {
            // given
            ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .lastModifiedDate(new Date())
                .participants(Arrays.asList(new Participant(userA, new Date(), Blocked.UNBLOCK),
                    new Participant(userB, new Date(), Blocked.UNBLOCK)))
                .createdDate(new Date())
                .build();
            chatRoomRepository.save(chatRoom);

            // when
            UserWithBlockDto userAWithBlock = new UserWithBlockDto(userA, Blocked.UNBLOCK);
            UserWithBlockDto userBWithBlock = new UserWithBlockDto(userB, Blocked.UNBLOCK);

            ChatRoomDto chatRoomDto = stompService.getOrCreateChatRoomDto(userAWithBlock,
                userBWithBlock);

            // then
            ChatRoom findChatRoom = chatRoomRepository.findByChatRoomNo(
                chatRoomDto.getChatRoomNo()).get();

            assertThat(findChatRoom).isEqualTo(chatRoom);
        }

        @DisplayName("두 유저의 채팅방이 존재하지 않을 시, 서로 차단하지 않은 경우 올바른 차단정보가 담긴 채팅방 생성 성공")
        @Test
        void successCreateChatRoomWhenNoBlock() {
            // given
            UserWithBlockDto userAWithBlock = new UserWithBlockDto(userA, Blocked.UNBLOCK);
            UserWithBlockDto userBWithBlock = new UserWithBlockDto(userB, Blocked.UNBLOCK);

            // when
            ChatRoomDto chatRoomDto = stompService.getOrCreateChatRoomDto(userAWithBlock,
                userBWithBlock);

            // then
            Optional<ChatRoom> findChatRoom = chatRoomRepository.findByUsers(userA, userB);
            assertThat(findChatRoom).isPresent();
            assertThat(findChatRoom.get().getChatRoomNo()).isEqualTo(chatRoomDto.getChatRoomNo());
            assertThat(findChatRoom.get().getParticipants()).allMatch(
                participant -> participant.getBlockedStatus() == Blocked.UNBLOCK);
        }

        @DisplayName("두 유저의 채팅방이 존재하지 않을 시, 한쪽만 차단한 경우 올바른 차단정보가 담긴 채팅방 생성 성공")
        @Test
        void successCreateChatRoomWhenOneBlock() {
            UserWithBlockDto userAWithBlock = new UserWithBlockDto(userA, Blocked.UNBLOCK);
            UserWithBlockDto userBWithBlock = new UserWithBlockDto(userB, Blocked.BLOCK);

            // when
            ChatRoomDto chatRoomDto = stompService.getOrCreateChatRoomDto(userAWithBlock,
                userBWithBlock);

            // then
            Optional<ChatRoom> findChatRoom = chatRoomRepository.findByUsers(userA, userB);
            assertThat(findChatRoom).isPresent();
            assertThat(findChatRoom.get().getChatRoomNo()).isEqualTo(chatRoomDto.getChatRoomNo());
            List<Participant> participants = findChatRoom.get().getParticipants();
            assertThat(participants).contains(new Participant(userB, null, Blocked.BLOCK))
                .contains(new Participant(userB, null, Blocked.BLOCK));
        }

        @DisplayName("두 유저의 채팅방이 존재하지 않을 시, 둘 다 차단한 경우 채팅방 생성 실패")
        @Test
        void failCreateChatRoom() {
            // given
            UserWithBlockDto userAWithBlock = new UserWithBlockDto(userA, Blocked.BLOCK);
            UserWithBlockDto userBWithBlock = new UserWithBlockDto(userB, Blocked.BLOCK);

            // when & then
            assertThatThrownBy(
                () -> stompService.getOrCreateChatRoomDto(userAWithBlock, userBWithBlock))
                .isInstanceOf(BaseException.class)
                .satisfies(exception -> {
                    assertThat(((BaseException) exception).getErrorCode()).isEqualTo(
                        ErrorCode.NOT_ALLOWED_CREATE_CHAT_ROOM);
                })
                .hasMessageContaining(ErrorMessage.NOT_ALLOWED_CREATE_CHAT_ROOM);
        }

    }

    @DisplayName("채팅방 나갈 때")
    @Nested
    class exitChatRoom {

        private User userA;
        private User userB;
        private ChatRoom chatRoom;

        @BeforeEach
        void saveUserAndChatRoom() {
            userA = User.builder()
                .actualUserId(1L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("uni")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(userA);

            userB = User.builder()
                .actualUserId(2L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("inu")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(userB);

            chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .lastModifiedDate(new Date())
                .participants(Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                    new Participant(userB, null, Blocked.UNBLOCK)))
                .createdDate(new Date())
                .build();
            chatRoomRepository.save(chatRoom);
        }

        @DisplayName("아무도 채팅방을 나가지 않은 상태라면 exitDate 변경 성공")
        @Test
        void exitChatRoomWhenNoBodyExit() {
            // given
            //      X

            // when
            stompService.exitChatRoom(userA, chatRoom);

            // then
            Optional<ChatRoom> findChatRoom = chatRoomRepository.findByChatRoomNo(
                chatRoom.getChatRoomNo());
            assertThat(findChatRoom).isPresent();
            Optional<Participant> participantA = findChatRoom.get().getParticipants().stream()
                .filter(participant -> participant.getUser().equals(userA))
                .findAny();
            assertThat(participantA).isPresent();
        }

        @DisplayName("상대방이 채팅방을 나갔다면 채팅방과 채팅내역 모두 삭제")
        @Test
        void exitChatRoomWhenOtherExit() {
            // given
            stompService.exitChatRoom(userB, chatRoom);

            // when
            stompService.exitChatRoom(userA, chatRoom);

            // then
            assertThat(chatRoomRepository.findByChatRoomNo(chatRoom.getChatRoomNo())).isEmpty();
            assertThat(chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo())).isEmpty();
        }
    }

    @DisplayName("채팅방 리스트 조회 시")
    @Nested
    class findChatRooms {

        private User userA;
        private User userB;
        private User userC;
        private User userD;

        @BeforeEach
        void saveUser() {
            userA = User.builder()
                .actualUserId(1L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("uni")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(userA);

            userB = User.builder()
                .actualUserId(2L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("uin")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(userB);

            userC = User.builder()
                .actualUserId(3L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("inu")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(userC);

            userD = User.builder()
                .actualUserId(4L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("iun")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(userD);
        }

        @DisplayName("모든 채팅 상대를 차단하지 않았을 경우 내가 속한 모든 채팅방 조회 성공")
        @Test
        void successToGetAllChatRoom() {
            // given
            saveChatRoomWithBlocked(1L, userA, userB, Blocked.UNBLOCK,
                Blocked.BLOCK);
            saveChatRoomWithBlocked(2L, userA, userC, Blocked.UNBLOCK,
                Blocked.UNBLOCK);
            saveChatRoomWithBlocked(3L, userA, userD, Blocked.UNBLOCK,
                Blocked.BLOCK);

            // when
            List<ChatRoomDto> chatRoomsJoined = stompService.getChatRoomsJoined(userA);

            // then
            List<Long> chatRoomIds = chatRoomsJoined.stream()
                .map(ChatRoomDto::getChatRoomNo)
                .toList();

            assertThat(chatRoomIds.size()).isEqualTo(3);
            assertThat(chatRoomIds).contains(1L);
            assertThat(chatRoomIds).contains(2L);
            assertThat(chatRoomIds).contains(3L);
        }

        @DisplayName("채팅 상대를 차단하지 않은 채팅방만 조회 성공 ")
        @Test
        void successToGetNonBlockChatRoom() {
            // given
            saveChatRoomWithBlocked(1L, userA, userB, Blocked.UNBLOCK,
                Blocked.BLOCK);
            saveChatRoomWithBlocked(2L, userA, userC, Blocked.BLOCK,
                Blocked.UNBLOCK);
            saveChatRoomWithBlocked(3L, userA, userD, Blocked.UNBLOCK,
                Blocked.BLOCK);

            // when
            List<ChatRoomDto> chatRoomsJoined = stompService.getChatRoomsJoined(userA);

            // then
            List<Long> chatRoomIds = chatRoomsJoined.stream()
                .map(ChatRoomDto::getChatRoomNo)
                .toList();

            assertThat(chatRoomIds.size()).isEqualTo(2);
            assertThat(chatRoomIds).contains(1L);
            assertThat(chatRoomIds).contains(3L);
        }

        @DisplayName("모든 채팅 상대를 차단한 경우 빈 채팅방 리스트 가져옴")
        @Test
        void getEmptyChatRooms() {
            // given
            saveChatRoomWithBlocked(1L, userA, userB, Blocked.BLOCK,
                Blocked.UNBLOCK);
            saveChatRoomWithBlocked(2L, userA, userC, Blocked.BLOCK,
                Blocked.UNBLOCK);
            saveChatRoomWithBlocked(3L, userA, userD, Blocked.BLOCK,
                Blocked.UNBLOCK);

            // when
            List<ChatRoomDto> chatRoomsJoined = stompService.getChatRoomsJoined(userA);

            // then
            assertThat(chatRoomsJoined).isEmpty();
        }
    }

    private void saveChatRoomWithBlocked(Long chatRoomNo, User user1, User user2,
        Blocked user1Blocked, Blocked user2Blocked) {
        ChatRoom chatRoom = ChatRoom.builder()
            .chatRoomNo(chatRoomNo)
            .lastModifiedDate(new Date())
            .participants(Arrays.asList(new Participant(user1, null, user1Blocked),
                new Participant(user2, null, user2Blocked)))
            .createdDate(new Date())
            .build();
        chatRoomRepository.save(chatRoom);
    }


}


