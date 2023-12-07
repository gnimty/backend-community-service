package com.gnimty.communityapiserver.domain.chat.service;


import static org.assertj.core.api.Assertions.*;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.auth.AuthStateCacheable;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
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
            User user = User.builder().actualUserId(1L).tier(Tier.gold).division(3)
                .summonerName("uni").status(Status.ONLINE).lp(3L).build();
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
            userA = User.builder().actualUserId(1L).tier(Tier.gold).division(3).summonerName("uni")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userA);

            userB = User.builder().actualUserId(2L).tier(Tier.gold).division(3).summonerName("inu")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userB);
        }

        @DisplayName("두 유저의 채팅방이 존재한 경우, 기존의 채팅방 조회 성공")
        @Test
        void successGetExistingChatRoom() {
            // given
            ChatRoom chatRoom = ChatRoom.builder().chatRoomNo(1L).lastModifiedDate(new Date())
                .participants(Arrays.asList(new Participant(userA, new Date(), Blocked.UNBLOCK),
                    new Participant(userB, new Date(), Blocked.UNBLOCK))).createdDate(new Date())
                .build();
            chatRoomRepository.save(chatRoom);

            // when
            UserWithBlockDto userAWithBlock = new UserWithBlockDto(userA, Blocked.UNBLOCK);
            UserWithBlockDto userBWithBlock = new UserWithBlockDto(userB, Blocked.UNBLOCK);

            ChatRoomDto chatRoomDto = stompService.getOrCreateChatRoomDto(userAWithBlock,
                userBWithBlock);

            // then
            ChatRoom findChatRoom = chatRoomRepository.findByChatRoomNo(chatRoomDto.getChatRoomNo())
                .get();

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
            assertThatThrownBy(() -> stompService.getOrCreateChatRoomDto(userAWithBlock,
                userBWithBlock)).isInstanceOf(BaseException.class).satisfies(exception -> {
                assertThat(((BaseException) exception).getErrorCode()).isEqualTo(
                    ErrorCode.NOT_ALLOWED_CREATE_CHAT_ROOM);
            }).hasMessageContaining(ErrorMessage.NOT_ALLOWED_CREATE_CHAT_ROOM);
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
            userA = User.builder().actualUserId(1L).tier(Tier.gold).division(3).summonerName("uni")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userA);

            userB = User.builder().actualUserId(2L).tier(Tier.gold).division(3).summonerName("inu")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userB);

            chatRoom = ChatRoom.builder().chatRoomNo(1L).lastModifiedDate(new Date()).participants(
                Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                    new Participant(userB, null, Blocked.UNBLOCK))).createdDate(new Date()).build();
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
                .filter(participant -> participant.getUser().equals(userA)).findAny();
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
            userA = User.builder().actualUserId(1L).tier(Tier.gold).division(3).summonerName("uni")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userA);

            userB = User.builder().actualUserId(2L).tier(Tier.gold).division(3).summonerName("uin")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userB);

            userC = User.builder().actualUserId(3L).tier(Tier.gold).division(3).summonerName("inu")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userC);

            userD = User.builder().actualUserId(4L).tier(Tier.gold).division(3).summonerName("iun")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userD);
        }

        @DisplayName("모든 채팅 상대를 차단하지 않았을 경우 내가 속한 모든 채팅방 조회 성공")
        @Test
        void successToGetAllChatRoom() {
            // given
            saveChatRoomWithBlocked(1L, userA, userB, Blocked.UNBLOCK, Blocked.BLOCK);
            saveChatRoomWithBlocked(2L, userA, userC, Blocked.UNBLOCK, Blocked.UNBLOCK);
            saveChatRoomWithBlocked(3L, userA, userD, Blocked.UNBLOCK, Blocked.BLOCK);

            // when
            List<ChatRoomDto> chatRoomsJoined = stompService.getChatRoomsJoined(userA);

            // then
            List<Long> chatRoomIds = chatRoomsJoined.stream().map(ChatRoomDto::getChatRoomNo)
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
            saveChatRoomWithBlocked(1L, userA, userB, Blocked.UNBLOCK, Blocked.BLOCK);
            saveChatRoomWithBlocked(2L, userA, userC, Blocked.BLOCK, Blocked.UNBLOCK);
            saveChatRoomWithBlocked(3L, userA, userD, Blocked.UNBLOCK, Blocked.BLOCK);

            // when
            List<ChatRoomDto> chatRoomsJoined = stompService.getChatRoomsJoined(userA);

            // then
            List<Long> chatRoomIds = chatRoomsJoined.stream().map(ChatRoomDto::getChatRoomNo)
                .toList();

            assertThat(chatRoomIds.size()).isEqualTo(2);
            assertThat(chatRoomIds).contains(1L);
            assertThat(chatRoomIds).contains(3L);
        }

        @DisplayName("모든 채팅 상대를 차단한 경우 빈 채팅방 리스트 가져옴")
        @Test
        void getEmptyChatRooms() {
            // given
            saveChatRoomWithBlocked(1L, userA, userB, Blocked.BLOCK, Blocked.UNBLOCK);
            saveChatRoomWithBlocked(2L, userA, userC, Blocked.BLOCK, Blocked.UNBLOCK);
            saveChatRoomWithBlocked(3L, userA, userD, Blocked.BLOCK, Blocked.UNBLOCK);

            // when
            List<ChatRoomDto> chatRoomsJoined = stompService.getChatRoomsJoined(userA);

            // then
            assertThat(chatRoomsJoined).isEmpty();
        }
    }

    @DisplayName("채팅 리스트 조회 시")
    @Nested
    class getChats {

        private User userA;
        private User userB;
        private ChatRoom chatRoom;

        @BeforeEach
        void saveUserAndChatRoom() {
            userA = User.builder().actualUserId(1L).tier(Tier.gold).division(3).summonerName("uni")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userA);

            userB = User.builder().actualUserId(2L).tier(Tier.gold).division(3).summonerName("inu")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userB);

            chatRoom = ChatRoom.builder().chatRoomNo(1L).lastModifiedDate(new Date()).participants(
                Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                    new Participant(userB, null, Blocked.UNBLOCK))).createdDate(new Date()).build();
            chatRoomRepository.save(chatRoom);
        }

        @AfterEach
        void deleteChats() {
            chatRepository.deleteAll();
        }

        @DisplayName("채팅방을 나간 이후 채팅이 없다면 빈 리스트 가져옴")
        @Test
        void getEmptyChatsAfterExitChatRoom() {
            // given
            chatRepository.save(
                Chat.builder().senderId(userA.getActualUserId()).sendDate(new Date()).message("hi")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            chatRepository.save(
                Chat.builder().senderId(userB.getActualUserId()).sendDate(new Date()).message("bye")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            Participant participantUserA = stompService.extractParticipant(userA,
                chatRoom.getParticipants(), true);
            participantUserA.setExitDate(new Date());
            chatRoomRepository.update(chatRoom);

            // when
            List<ChatDto> chats = stompService.getChatList(userA, chatRoom);

            // then
            assertThat(chats).isEmpty();
        }

        @DisplayName("채팅방을 나간 이후 채팅이 1개 이상이라면 나간 이후의 채팅 가져옴")
        @Test
        void getSomeChatsAfterExitChatRoom() {
            // given
            chatRepository.save(
                Chat.builder().senderId(userA.getActualUserId()).sendDate(new Date()).message("hi")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            chatRepository.save(
                Chat.builder().senderId(userB.getActualUserId()).sendDate(new Date()).message("bye")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            Participant participantUserA = stompService.extractParticipant(userA,
                chatRoom.getParticipants(), true);
            participantUserA.setExitDate(new Date());
            chatRoomRepository.update(chatRoom);

            chatRepository.save(
                Chat.builder().senderId(userB.getActualUserId()).sendDate(new Date())
                    .message("bye2").chatRoomNo(chatRoom.getChatRoomNo()).build());

            // when
            List<ChatDto> chats = stompService.getChatList(userA, chatRoom);

            // then
            assertThat(chats.size()).isEqualTo(1);
            assertThat(chats.get(0).getMessage()).isEqualTo("bye2");
        }

        @DisplayName("채팅이 없다면 빈 List<ChatDto> 가져옴")
        @Test
        void getEmptyChats() {
            // given
            //      X

            // when
            List<ChatDto> chats = stompService.getChatList(userA, chatRoom);

            // then
            assertThat(chats).isEmpty();
        }

        @DisplayName("채팅이 있다면 모든 채팅을 가져옴")
        @Test
        void getAllChats() {
            // given
            chatRepository.save(
                Chat.builder().senderId(userA.getActualUserId()).sendDate(new Date()).message("hi")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            chatRepository.save(
                Chat.builder().senderId(userB.getActualUserId()).sendDate(new Date()).message("bye")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            // when
            List<ChatDto> chats = stompService.getChatList(userA, chatRoom);

            // then
            assertThat(chats.size()).isEqualTo(2);
        }
    }

    private void saveChatRoomWithBlocked(Long chatRoomNo, User user1, User user2,
        Blocked user1Blocked, Blocked user2Blocked) {
        ChatRoom chatRoom = ChatRoom.builder().chatRoomNo(chatRoomNo).lastModifiedDate(new Date())
            .participants(Arrays.asList(new Participant(user1, null, user1Blocked),
                new Participant(user2, null, user2Blocked))).createdDate(new Date()).build();
        chatRoomRepository.save(chatRoom);
    }

    @DisplayName("채팅 보낼 시")
    @Nested
    class saveChat {

        @DisplayName("채팅이 저장되고 해당 채팅방의 마지막 수정일자가 수정됨")
        @Test
        void saveChatAndUpdateChatRoom() {
            // given
            User userA = User.builder().actualUserId(1L).tier(Tier.gold).division(3)
                .summonerName("uni").status(Status.ONLINE).lp(3L).build();
            userRepository.save(userA);

            User userB = User.builder().actualUserId(2L).tier(Tier.gold).division(3)
                .summonerName("inu").status(Status.ONLINE).lp(3L).build();
            userRepository.save(userB);

            ChatRoom chatRoom = ChatRoom.builder().chatRoomNo(1L).lastModifiedDate(new Date())
                .participants(Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                    new Participant(userB, null, Blocked.UNBLOCK))).createdDate(new Date()).build();
            chatRoomRepository.save(chatRoom);

            // when
            ChatDto chatDto = stompService.saveChat(userA, chatRoom, "hi");

            // then
            List<Chat> chats = chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo());
            assertThat(chats).isNotEmpty();
            assertThat(chats.get(0).getMessage()).isEqualTo("hi");
            ChatRoom findChatRoom = chatRoomRepository.findByChatRoomNo(chatRoom.getChatRoomNo())
                .get();
            assertThat(findChatRoom.getLastModifiedDate()).isEqualTo(chatDto.getSendDate());
        }
    }

    @DisplayName("채팅 읽을 시")
    @Nested
    class readChats {

        private User userA;
        private User userB;
        private ChatRoom chatRoom;

        @BeforeEach
        void saveUserAndChatRoom() {
            userA = User.builder().actualUserId(1L).tier(Tier.gold).division(3).summonerName("uni")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userA);

            userB = User.builder().actualUserId(2L).tier(Tier.gold).division(3).summonerName("inu")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userB);

            chatRoom = ChatRoom.builder().chatRoomNo(1L).lastModifiedDate(new Date()).participants(
                Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                    new Participant(userB, null, Blocked.UNBLOCK))).createdDate(new Date()).build();
            chatRoomRepository.save(chatRoom);

            for (int i = 0; i < 5; i++) {
                chatRepository.save(
                    Chat.builder().chatRoomNo(chatRoom.getChatRoomNo()).message("hi")
                        .sendDate(new Date()).senderId(userA.getActualUserId()).build());
            }
        }

        @AfterEach
        void deleteChats() {
            chatRepository.deleteAll();
        }

        @DisplayName("읽지 않은 메시지가 있었다면 해당 채팅방에서 상대방이 보낸 채팅의 readCount가 모두 0이 됨")
        @Test
        void hasUnReadMessages() {
            // given
            for (int i = 0; i < 5; i++) {
                chatRepository.save(
                    Chat.builder().chatRoomNo(chatRoom.getChatRoomNo()).message("hi")
                        .sendDate(new Date()).senderId(userB.getActualUserId()).build());
            }

            // when
            stompService.readOtherChats(userA, chatRoom);

            // then
            List<Chat> otherChats = chatRepository.findBySenderIdAndChatRoomNo(
                userB.getActualUserId(), chatRoom.getChatRoomNo());
            otherChats.stream().map(Chat::getReadCnt)
                .forEach(readCount -> assertThat(readCount).isEqualTo(0));
        }

        @DisplayName("상대방이 보낸 채팅이 없어도 아무일도 일어나지 않음")
        @Test
        void hasNoOtherChats() {
            // given
            //      X

            // when & then
            assertThatCode(() -> stompService.readOtherChats(userA, chatRoom))
                .doesNotThrowAnyException();
        }
    }

    @DisplayName("사용자의 접속 상태가 변동될 시")
    @Nested
    class changeUserConnectStatus {

        @DisplayName("변동된 접속상태로 변경")
        @Test
        void updateUser() {
            // given
            User userA = User.builder().actualUserId(1L).tier(Tier.gold).division(3)
                .summonerName("uni")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userA);

            // when
            stompService.updateConnStatus(userA, Status.AWAY);

            // then
            User findUserA = userRepository.findByActualUserId(userA.getActualUserId()).get();
            assertThat(findUserA.getStatus()).isEqualTo(Status.AWAY);
        }
    }

    @DisplayName("상대방 차단 시")
    @Nested
    class blockOtherUser {

        private User userA;
        private User userB;

        @BeforeEach
        void saveUsers() {
            userA = User.builder().actualUserId(1L).tier(Tier.gold).division(3).summonerName("uni")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userA);

            userB = User.builder().actualUserId(2L).tier(Tier.gold).division(3).summonerName("inu")
                .status(Status.ONLINE).lp(3L).build();
            userRepository.save(userB);
        }

        @AfterEach
        void deleteChatRoomAndChats() {
            chatRepository.deleteAll();
            chatRoomRepository.deleteAll();
        }

        @DisplayName("상대방이 채팅방을 나가지 않았다면, 채팅방의 차단 정보가 수정 됨")
        @Test
        void updateChatRoomBlock() {
            // given
            ChatRoom chatRoom = ChatRoom.builder().chatRoomNo(1L).lastModifiedDate(new Date())
                .participants(
                    Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                        new Participant(userB, null, Blocked.UNBLOCK))).createdDate(new Date())
                .build();
            chatRoomRepository.save(chatRoom);

            // when
            stompService.updateBlockStatus(userA, userB, Blocked.BLOCK);

            // then
            Optional<ChatRoom> findChatRoom = chatRoomRepository.findByChatRoomNo(
                chatRoom.getChatRoomNo());
            assertThat(findChatRoom).isPresent();

            Optional<Participant> userAParticipant = findChatRoom.get().getParticipants().stream()
                .filter(participant -> participant.getUser().equals(userA))
                .findFirst();
            assertThat(userAParticipant).isPresent();

            assertThat(userAParticipant.get().getBlockedStatus()).isEqualTo(Blocked.BLOCK);
        }

        @DisplayName("상대방이 채팅방을 나갔고 이후의 채팅이 있을 때, 채팅방의 차단 정보가 수정 됨")
        @Test
        void() {
            // given

            // when

            // then

        }

        @DisplayName("상대방이 채팅방을 나갔고 이후의 채팅이 없을 때, 채팅방과 해당 채팅방의 채팅이 모두 삭제됨")
        @Test
        void() {
            // given

            // when

            // then

        }

        @DisplayName("상대방과의 채팅방이 있고 상대방도 나를 차단했을 때, 채팅방과 해당 채팅방의 채팅이 모두 삭제됨")
        @Test
        void deleteChatRoomAndChatsWhenUpdatingBlock() {
            // given
            ChatRoom chatRoom = ChatRoom.builder().chatRoomNo(1L).lastModifiedDate(new Date())
                .participants(
                    Arrays.asList(new Participant(userA, null, Blocked.UNBLOCK),
                        new Participant(userB, null, Blocked.BLOCK))).createdDate(new Date())
                .build();
            chatRoomRepository.save(chatRoom);

            // when
            stompService.updateBlockStatus(userA, userB, Blocked.BLOCK);

            // then
            assertThat(chatRoomRepository.findByUsers(userA, userB)).isEmpty();
            assertThat(chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo())).isEmpty();
        }


        @DisplayName("상대방과의 채팅방이 없다면, 아무일도 일어나지 않음")
        @Test
        void() {
            // given

            // when

            // then

        }
    }
}


