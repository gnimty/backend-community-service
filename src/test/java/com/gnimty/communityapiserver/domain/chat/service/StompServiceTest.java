package com.gnimty.communityapiserver.domain.chat.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@Slf4j
@SpringBootTest
@ActiveProfiles(value = "test")
public class StompServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private StompService stompService;

    @BeforeEach
    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
        chatRoomRepository.deleteAll();
        chatRepository.deleteAll();
    }


    @DisplayName("채팅방 생성 또는 조회 시")
    @Nested
    class getOrCreateChatRoom {

        private User userA;
        private User userB;

        @BeforeEach
        void saveUsers() {
            userA = createUser("uni", 1L);
            userRepository.save(userA);

            userB = createUser("inu", 2L);
            userRepository.save(userB);
        }

        @DisplayName("두 유저의 채팅방이 존재한다면, 기존 채팅방 조회")
        @Test
        void successGetExistingChatRoom() {
            // given
            ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .participants(Arrays.asList(new Participant(userA, Blocked.UNBLOCK), new Participant(userB, Blocked.UNBLOCK)))
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

        @DisplayName("두 유저의 채팅방이 존재하지 않다면, 서로 차단하지 않은 경우 올바른 차단정보가 담긴 채팅방 생성")
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

        @DisplayName("두 유저의 채팅방이 존재하지 않다면, 한쪽만 차단한 경우 올바른 차단정보가 담긴 채팅방 생성")
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
            assertThat(participants).contains(new Participant(userB, Blocked.BLOCK))
                .contains(new Participant(userB, Blocked.BLOCK));
        }

        @DisplayName("두 유저의 채팅방이 존재하지 않다면, 둘 다 차단한 경우 채팅방 생성 실패")
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

    @DisplayName("채팅방 나갈 시")
    @Nested
    class exitChatRoom {

        private User userA;
        private User userB;
        private ChatRoom chatRoom;

        @BeforeEach
        void saveUserAndChatRoom() {
            userA = createUser("uni", 1L);
            userRepository.save(userA);

            userB = createUser("inu", 2L);
            userRepository.save(userB);

            chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .participants(
                    Arrays.asList(new Participant(userA, Blocked.UNBLOCK),
                        new Participant(userB, Blocked.UNBLOCK)))
                .build();
            chatRoomRepository.save(chatRoom);
        }

        @AfterEach
        void deleteChatRoom() {
            chatRoomRepository.deleteAll();
        }

        @DisplayName("아무도 채팅방을 나가지 않은 상태라면, exitDate 변경됨")
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

        @DisplayName("상대방이 이미 채팅방을 나갔다면, 채팅방과 채팅내역 모두 삭제됨")
        @Test
        void exitChatRoomWhenOtherExit() {
            // given
            chatRoom.getParticipants().stream()
                .filter(participant -> participant.getUser().equals(userB))
                .forEach(participant -> participant.outChatRoom());
            chatRoomRepository.save(chatRoom);

            // when
            stompService.exitChatRoom(userA, chatRoom);

            // then
            assertThat(chatRoomRepository.findByChatRoomNo(chatRoom.getChatRoomNo())).isEmpty();
            assertThat(chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo(), ChatDto.class).isEmpty());
        }

        @DisplayName("상대방이 나를 차단했다면, 채팅방과 채팅내역 모두 삭제됨")
        @Test
        void exitChatRoomWhenBlocked() {
            // given
            chatRoom.getParticipants().stream()
                .filter(participant -> participant.getUser().equals(userB))
                .forEach(participant -> participant.updateBlockedStatus(Blocked.BLOCK));
            chatRoomRepository.save(chatRoom);

            // when
            stompService.exitChatRoom(userA, chatRoom);

            // then
            assertThat(chatRoomRepository.findByChatRoomNo(chatRoom.getChatRoomNo())).isEmpty();
            assertThat(chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo(), ChatDto.class)).isEmpty();
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
            userA = createUser("uniA", 1L);
            userRepository.save(userA);

            userB = createUser("uniB", 2L);
            userRepository.save(userB);

            userC = createUser("uniC", 3L);
            userRepository.save(userC);

            userD = createUser("uniD", 4L);
            userRepository.save(userD);
        }

        @DisplayName("모든 채팅 상대를 차단했다면, 빈 채팅방 리스트 가져옴")
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

        @DisplayName("모든 채팅 상대를 차단하지 않았다면, 내가 속한 모든 채팅방 조회")
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

        @DisplayName("채팅 상대를 차단하지 않은 채팅방만 조회")
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


    }

    @DisplayName("채팅 내역 조회 시")
    @Nested
    class getChats {

        private User userA;
        private User userB;
        private ChatRoom chatRoom;

        @BeforeEach
        void saveUserAndChatRoom() {
            userA = createUser("uni", 1L);
            userRepository.save(userA);

            userB = createUser("inu", 2L);
            userRepository.save(userB);

            chatRoom = ChatRoom.builder().chatRoomNo(1L).participants(
                Arrays.asList(new Participant(userA, Blocked.UNBLOCK),
                    new Participant(userB, Blocked.UNBLOCK))).build();
            chatRoomRepository.save(chatRoom);
        }

        @AfterEach
        void deleteChats() {
            chatRepository.deleteAll();
        }

        @DisplayName("채팅방을 나간 이후 채팅이 없다면, 빈 리스트 가져옴")
        @Test
        void getEmptyChatsAfterExitChatRoom() throws InterruptedException {
            // given
            chatRepository.save(
                Chat.builder().senderId(userA.getActualUserId()).message("hi")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            chatRepository.save(
                Chat.builder().senderId(userB.getActualUserId()).message("bye")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            Participant participantUserA = stompService.extractParticipant(userA,
                chatRoom.getParticipants(), true);

            Thread.sleep(10);
            participantUserA.outChatRoom();
            chatRoomRepository.update(chatRoom);

            // when
            List<ChatDto> chats = stompService.getChatList(userA, chatRoom);

            // then
            assertThat(chats).isEmpty();
        }

        @DisplayName("채팅방을 나간 이후 채팅이 있다면, 나간 이후의 채팅들만 가져옴")
        @Test
        void getSomeChatsAfterExitChatRoom() throws InterruptedException {
            // given
            chatRepository.save(
                Chat.builder().senderId(userA.getActualUserId()).message("hi")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            chatRepository.save(
                Chat.builder().senderId(userB.getActualUserId()).message("bye")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            Thread.sleep(10);
            Participant participantUserA = stompService.extractParticipant(userA,
                chatRoom.getParticipants(), true);
            participantUserA.outChatRoom();
            chatRoomRepository.update(chatRoom);

            chatRepository.save(
                Chat.builder().senderId(userB.getActualUserId())
                    .message("bye2").chatRoomNo(chatRoom.getChatRoomNo()).build());

            // when
            List<ChatDto> chats = stompService.getChatList(userA, chatRoom);

            // then
            assertThat(chats.size()).isEqualTo(1);
            assertThat(chats.get(0).getMessage()).isEqualTo("bye2");
        }

        @DisplayName("채팅방을 나가지 않고 채팅이 존재한다면, 모든 채팅을 가져옴")
        @Test
        void getAllChats() {
            // given
            chatRepository.save(
                Chat.builder().senderId(userA.getActualUserId()).message("hi")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            chatRepository.save(
                Chat.builder().senderId(userB.getActualUserId()).message("bye")
                    .chatRoomNo(chatRoom.getChatRoomNo()).build());

            // when
            List<ChatDto> chats = stompService.getChatList(userA, chatRoom);

            // then
            assertThat(chats.size()).isEqualTo(2);
        }

        @DisplayName("채팅이 없다면, 빈 List<ChatDto> 가져옴")
        @Test
        void getEmptyChats() {
            // given
            //      X

            // when
            List<ChatDto> chats = stompService.getChatList(userA, chatRoom);

            // then
            assertThat(chats).isEmpty();
        }

    }

    private void saveChatRoomWithBlocked(Long chatRoomNo, User user1, User user2,
                                         Blocked user1Blocked, Blocked user2Blocked) {
        ChatRoom chatRoom = ChatRoom.builder()
            .chatRoomNo(chatRoomNo)
            .participants(Arrays.asList(new Participant(user1, user1Blocked),
                new Participant(user2, user2Blocked)))
            .build();
        chatRoomRepository.save(chatRoom);
    }

    @DisplayName("채팅 보낼 시")
    @Nested
    class saveChat {

        @DisplayName("채팅이 저장되고 해당 채팅방의 마지막 수정일자가 수정됨")
        @Test
        void saveChatAndUpdateChatRoom() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .participants(Arrays.asList(new Participant(userA, Blocked.UNBLOCK),
                    new Participant(userB, Blocked.UNBLOCK)))
                .build();
            chatRoomRepository.save(chatRoom);

            // when
            ChatDto chatDto = stompService.saveChat(userA, chatRoom, "hi");

            // then
            List<ChatDto> chats = chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo(), ChatDto.class);
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
        void saveUsersAndChatRoom() {
            userA = createUser("uni", 1L);
            userRepository.save(userA);

            userB = createUser("inu", 2L);
            userRepository.save(userB);

            chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .participants(Arrays.asList(new Participant(userA, Blocked.UNBLOCK),
                    new Participant(userB, Blocked.UNBLOCK)))
                .build();
            chatRoomRepository.save(chatRoom);

            for (int i = 0; i < 5; i++) {
                chatRepository.save(
                    Chat.builder()
                        .chatRoomNo(chatRoom.getChatRoomNo())
                        .message("hi")
                        .senderId(userA.getActualUserId())
                        .build());
            }
        }

        @AfterEach
        void deleteChats() {
            chatRepository.deleteAll();
        }

        @DisplayName("읽지 않은 메시지가 있다면, 해당 채팅방에서 상대방이 보낸 채팅의 readCount가 모두 0이 됨")
        @Test
        void hasUnReadMessages() {
            // given
            for (int i = 0; i < 5; i++) {
                chatRepository.save(
                    Chat.builder().chatRoomNo(chatRoom.getChatRoomNo()).message("hi")
                        .senderId(userB.getActualUserId()).build());
            }

            // when
            stompService.readOtherChats(userA, chatRoom);

            // then
            List<Chat> otherChats = chatRepository.findBySenderIdAndChatRoomNo(
                userB.getActualUserId(), chatRoom.getChatRoomNo());
            otherChats.stream().map(Chat::getReadCnt)
                .forEach(readCount -> assertThat(readCount).isEqualTo(0));
        }

        @DisplayName("상대방이 보낸 채팅이 없을 시 아무일도 일어나지 않음")
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

        @DisplayName("사용자가 직접 변경 했다면, nowStatus, selectedStatus 둘다 변경됨")
        @Test
        void updateByUser() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            // when
            stompService.updateConnStatus(userA, Status.AWAY, true);

            // then
            User findUserA = userRepository.findByActualUserId(userA.getActualUserId()).get();
            assertThat(findUserA.getNowStatus()).isEqualTo(Status.AWAY);
            assertThat(findUserA.getSelectedStatus()).isEqualTo(Status.AWAY);
        }

        @DisplayName("상태가 자동 변경 됐다면, nowStatus가 selectedStatus값과 동일함")
        @Test
        void updateByConnect() {
            // given
            User userA = createUser("uni", 1L);
            userA.updateNowStatus(Status.OFFLINE);
            userA.updateSelectedStatus(Status.AWAY);
            userRepository.save(userA);

            // when
            stompService.updateConnStatus(userA, Status.ONLINE, false);

            // then
            User findUserA = userRepository.findByActualUserId(userA.getActualUserId()).get();
            assertThat(findUserA.getNowStatus()).isEqualTo(Status.AWAY);
        }

        @DisplayName("상태가 자동 변경 됐다면, nowStatus가 offline으로 바뀜")
        @Test
        void updateByDisConnect() {
            // given
            User userA = createUser("uni", 1L);
            userA.updateNowStatus(Status.ONLINE);
            userA.updateSelectedStatus(Status.ONLINE);
            userRepository.save(userA);

            // when
            stompService.updateConnStatus(userA, Status.OFFLINE, false);

            // then
            User findUserA = userRepository.findByActualUserId(userA.getActualUserId()).get();
            assertThat(findUserA.getNowStatus()).isEqualTo(Status.OFFLINE);
        }
    }

    @DisplayName("상대방 차단 시")
    @Nested
    class blockOtherUser {

        private User userA;
        private User userB;
        private ChatRoom chatRoom;

        @BeforeEach
        void saveUsersAndChatRoom() {
            userA = createUser("uni", 1L);
            userRepository.save(userA);

            userB = createUser("inu", 2L);
            userRepository.save(userB);

            chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .participants(
                    Arrays.asList(new Participant(userA, Blocked.UNBLOCK),
                        new Participant(userB, Blocked.UNBLOCK)))
                .build();
            chatRoomRepository.save(chatRoom);
        }

        @AfterEach
        void deleteChatRoomAndChats() {
            chatRepository.deleteAll();
            chatRoomRepository.deleteAll();
        }

        @DisplayName("상대방이 채팅방을 나가지 않았다면, 채팅방의 차단 정보가 수정 됨")
        @Test
        void updateChatRoomBlockWhenOtherNoExit() {
            // given
            //      X

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

        @DisplayName("상대방이 채팅방을 나갔고 이후의 채팅이 있다면, 채팅방의 차단 정보가 수정 됨")
        @Test
        void updateChatRoomBlockWhenOtherExitBeforeSomeChats() {
            // given
            chatRoom.getParticipants().stream()
                .filter(participant -> participant.getUser().equals(userB))
                .forEach(participant -> participant.outChatRoom());
            chatRoomRepository.save(chatRoom);

            Chat chat = chatRepository.save(Chat.builder()
                .senderId(userA.getActualUserId())
                .message("hi")
                .chatRoomNo(chatRoom.getChatRoomNo())
                .build());
            chatRoom.refreshModifiedDate(chat.getSendDate());
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

        @DisplayName("상대방이 채팅방을 나갔고 이후의 채팅이 없다면, 채팅방과 해당 채팅방의 채팅이 모두 삭제됨")
        @Test
        void deleteChatDataWhenOtherExit() throws InterruptedException {
            // given
            chatRoom.getParticipants().stream()
                .filter(participant -> participant.getUser().equals(userB))
                .forEach(participant -> participant.outChatRoom());
            chatRoomRepository.save(chatRoom);
            Thread.sleep(10);


            // when
            stompService.updateBlockStatus(userA, userB, Blocked.BLOCK);

            // then
            assertThat(chatRoomRepository.findByUsers(userA, userB)).isEmpty();
            assertThat(chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo(), ChatDto.class)).isEmpty();
        }

        @DisplayName("상대방과의 채팅방이 있고 상대방도 나를 차단했다면, 채팅방과 해당 채팅방의 채팅이 모두 삭제됨")
        @Test
        void deleteChatRoomAndChatsWhenUpdatingBlock() {
            // given
            chatRoom.getParticipants().stream()
                .filter(participant -> participant.getUser().equals(userB))
                .forEach(participant -> participant.updateBlockedStatus(Blocked.BLOCK));
            chatRoomRepository.save(chatRoom);

            // when
            stompService.updateBlockStatus(userA, userB, Blocked.BLOCK);

            // then
            assertThat(chatRoomRepository.findByUsers(userA, userB)).isEmpty();
            assertThat(chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo(), ChatDto.class)).isEmpty();
        }


        @DisplayName("상대방과의 채팅방이 없다면, 아무일도 일어나지 않음")
        @Test
        void hasNoChatRoom() {
            // given
            chatRoomRepository.delete(chatRoom);

            // when & then
            assertThatCode(() -> stompService.updateBlockStatus(userA, userB, Blocked.BLOCK))
                .doesNotThrowAnyException();
        }
    }

    @DisplayName("상대방 차단 해제 시")
    @Nested
    class unBlockOtherUser {

        private User userA;
        private User userB;

        private ChatRoom chatRoom;

        @BeforeEach
        void saveUsersAndChatRoom() {
            userA = createUser("uni", 1L);
            userRepository.save(userA);

            userB = createUser("inu", 2L);
            userRepository.save(userB);

            chatRoom = ChatRoom.builder().chatRoomNo(1L)
                .participants(
                    Arrays.asList(new Participant(userA, Blocked.BLOCK),
                        new Participant(userB, Blocked.UNBLOCK)))
                .build();
            chatRoomRepository.save(chatRoom);
        }

        @AfterEach
        void deleteChatRoomAndChats() {
            chatRepository.deleteAll();
            chatRoomRepository.deleteAll();
        }

        @DisplayName("상대방과의 채팅방이 존재한다면, 채팅방의 차단정보가 수정됨")
        @Test
        void updateChatRoomBlock() {
            // given
            //      X

            // when
            stompService.updateBlockStatus(userA, userB, Blocked.UNBLOCK);

            // then
            Optional<ChatRoom> findChatRoom = chatRoomRepository.findByChatRoomNo(
                chatRoom.getChatRoomNo());
            assertThat(findChatRoom).isPresent();

            Optional<Participant> userAParticipant = findChatRoom.get().getParticipants().stream()
                .filter(participant -> participant.getUser().equals(userA))
                .findFirst();
            assertThat(userAParticipant).isPresent();
            assertThat(userAParticipant.get().getBlockedStatus()).isEqualTo(Blocked.UNBLOCK);
        }

        @DisplayName("상대방과의 채팅방이 없다면, 아무일도 일어나지 않음")
        @Test
        void hasNoChatRoom() {
            // given
            chatRoomRepository.delete(chatRoom);

            // when & then
            assertThatCode(() -> stompService.updateBlockStatus(userA, userB, Blocked.UNBLOCK))
                .doesNotThrowAnyException();
        }
    }

    @DisplayName("상대방 차단여부 확인 시")
    @Nested
    class checkBlockStatus {

        private User userA;
        private User userB;
        private ChatRoom chatRoom;

        @BeforeEach
        void saveUsers() {
            userA = createUser("uni", 1L);
            userRepository.save(userA);

            userB = createUser("inu", 2L);
            userRepository.save(userB);
        }

        @AfterEach
        void deleteChatRoom() {
            chatRoomRepository.deleteAll();
        }

        @DisplayName("상대방이 나를 차단했을 경우 true 반환")
        @Test
        void checkBlock() {
            // given
            chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .participants(
                    Arrays.asList(new Participant(userA, Blocked.UNBLOCK),
                        new Participant(userB, Blocked.BLOCK)))
                .build();
            chatRoomRepository.save(chatRoom);

            // when
            boolean blockStatus = stompService.isBlockParticipant(chatRoom, userB);

            // then
            assertThat(blockStatus).isTrue();
        }

        @DisplayName("상대방이 나를 차단하지 않았을 경우 false 반환")
        @Test
        void checkUnBlock() {
            // given
            chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .participants(
                    Arrays.asList(new Participant(userA, Blocked.UNBLOCK),
                        new Participant(userB, Blocked.UNBLOCK)))
                .build();
            chatRoomRepository.save(chatRoom);

            // when
            boolean blockStatus = stompService.isBlockParticipant(chatRoom, userB);

            // then
            assertThat(blockStatus).isFalse();
        }
    }

    @DisplayName("탈퇴 시")
    @Nested
    class Withdrawal {

        @AfterEach
        void deleteUsers() {
            userRepository.deleteAll();
        }

        @DisplayName("사용자와 관련한 모든 데이터 삭제")
        @Test
        void deleteAllDate() {
            // given
            User userA = createUser("uni", 1L);
            userRepository.save(userA);

            User userB = createUser("inu", 2L);
            userRepository.save(userB);

            ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomNo(1L)
                .participants(
                    Arrays.asList(new Participant(userA, Blocked.BLOCK),
                        new Participant(userB, Blocked.UNBLOCK)))
                .build();
            chatRoomRepository.save(chatRoom);

            for (int i = 0; i < 5; i++) {
                chatRepository.save(Chat.builder()
                    .senderId(userA.getActualUserId())
                    .message("hi")
                    .chatRoomNo(chatRoom.getChatRoomNo())
                    .build());
            }

            // when
            stompService.withdrawal(userA.getActualUserId());

            // then
            assertThat(userRepository.findByActualUserId(userA.getActualUserId())).isEmpty();
            assertThat(chatRoomRepository.findByUser(userA)).isEmpty();
            assertThat(chatRepository.findByChatRoomNo(chatRoom.getChatRoomNo(), ChatDto.class)).isEmpty();
        }

        @DisplayName("유효하지 않은 userId여도 아무일도 일어나지 않음")
        @Test
        void hasNoUser() {
            // given
            //      X

            // when & then
            assertThatCode(() -> stompService.withdrawal(1L))
                .doesNotThrowAnyException();
        }
    }

    @DisplayName("라이엇 정보 변경 시")
    @Nested
    class updateRiotAccount {

        @AfterEach
        void deleteUser() {
            userRepository.deleteAll();
            memberRepository.deleteAll();
        }

        @DisplayName("User가 존재하면 라이엇 정보 업데이트 됨")
        @Test
        void updateProfileIconId() {
            // given
            Member member = new Member(true, "uni@naver.com", "afD23!", 1L, "uni", Status.ONLINE,
                1L);
            memberRepository.save(member);

            RiotAccount riotAccount = RiotAccount.builder()
                .member(member)
                .division(2)
                .frequentChampionId2(4L)
                .frequentChampionId3(5L)
                .build();

            User user = User.builder()
                .actualUserId(member.getId())
                .name("uni")
                .division(3)
                .build();

            userRepository.save(user);

            // when
            stompService.createOrUpdateUser(riotAccount);

            // then
            Optional<User> findUser = userRepository.findByActualUserId(user.getActualUserId());
            assertThat(findUser).isPresent();
            assertThat(findUser.get().getName()).isEqualTo("uni");
            assertThat(findUser.get().getDivision()).isEqualTo(2);
            assertThat(findUser.get().getFrequentChampionId2()).isEqualTo(4L);
            assertThat(findUser.get().getFrequentChampionId3()).isEqualTo(5L);
        }

        @DisplayName("User가 없다면 User생성")
        @Test
        void createUserWhenNoUser() {
            // given
            Member member = new Member(true, "uni@naver.com", "afD23!", 1L, "uni", Status.ONLINE,
                1L);
            memberRepository.save(member);

            RiotAccount riotAccount = RiotAccount.builder()
                .member(member)
                .division(2)
                .frequentChampionId2(4L)
                .frequentChampionId3(5L)
                .build();

            // when
            stompService.createOrUpdateUser(riotAccount);

            // then
            assertThat(userRepository.findByActualUserId(member.getId())).isPresent();
        }
    }


    @DisplayName("라이엇 정보 bulk 변경 시")
    @Nested
    class bulkUpdateRiotAccount {

        @AfterEach
        void deleteUser() {
            userRepository.deleteAll();
            memberRepository.deleteAll();
        }

        @DisplayName("유저가 모두 존재 한다면 모든 유저 정보 수정")
        @Test
        void updateAllUser() {
            // given
            Member memberA = new Member(true, "uni1@naver.com", "afD23!", 1L, "uniA", Status.AWAY, 1L);
            Member memberB = new Member(true, "uni2@naver.com", "afD23!", 1L, "uniB", Status.AWAY, 1L);
            Member memberC = new Member(true, "uni3@naver.com", "afD23!", 1L, "uniC", Status.AWAY, 1L);

            memberRepository.save(memberA);
            memberRepository.save(memberB);
            memberRepository.save(memberC);

            RiotAccount riotAccountA = RiotAccount.builder()
                .member(memberA)
                .frequentChampionId1(3L)
                .frequentChampionId3(4L)
                .frequentLane1(Lane.JUNGLE)
                .build();
            RiotAccount riotAccountB = RiotAccount.builder()
                .member(memberB)
                .frequentChampionId1(3L)
                .frequentChampionId3(4L)
                .frequentLane1(Lane.JUNGLE)
                .build();
            RiotAccount riotAccountC = RiotAccount.builder()
                .member(memberC)
                .frequentChampionId1(3L)
                .frequentChampionId3(4L)
                .frequentLane1(Lane.JUNGLE)
                .build();
            List<RiotAccount> riotAccounts = new ArrayList<>(Arrays.asList(riotAccountA, riotAccountB, riotAccountC));

            User userA = createUser("userA", memberA.getId());
            User userB = createUser("userB", memberB.getId());
            User userC = createUser("userC", memberC.getId());
            userRepository.save(userA);
            userRepository.save(userB);
            userRepository.save(userC);

            // when
            stompService.createOrUpdateUser(riotAccounts);

            // then
            Optional<User> findUserA = userRepository.findByActualUserId(userA.getActualUserId());
            assertThat(findUserA).isPresent();
            assertThat(findUserA.get().getFrequentChampionId1()).isEqualTo(3L);
            assertThat(findUserA.get().getFrequentChampionId3()).isEqualTo(4L);
            assertThat(findUserA.get().getFrequentLane1()).isEqualTo(Lane.JUNGLE);
            assertThat(findUserA.get().getNowStatus()).isEqualTo(Status.ONLINE);

            Optional<User> findUserB = userRepository.findByActualUserId(userB.getActualUserId());
            assertThat(findUserB).isPresent();
            assertThat(findUserB.get().getFrequentChampionId1()).isEqualTo(3L);
            assertThat(findUserB.get().getFrequentChampionId3()).isEqualTo(4L);
            assertThat(findUserB.get().getFrequentLane1()).isEqualTo(Lane.JUNGLE);
            assertThat(findUserB.get().getNowStatus()).isEqualTo(Status.ONLINE);

            Optional<User> findUserC = userRepository.findByActualUserId(userC.getActualUserId());
            assertThat(findUserC).isPresent();
            assertThat(findUserC.get().getFrequentChampionId1()).isEqualTo(3L);
            assertThat(findUserC.get().getFrequentChampionId3()).isEqualTo(4L);
            assertThat(findUserC.get().getFrequentLane1()).isEqualTo(Lane.JUNGLE);
            assertThat(findUserC.get().getNowStatus()).isEqualTo(Status.ONLINE);
        }

        @DisplayName("유저들 중 존재 하지 않는 user가 있다면 저장, 나머지는 수정")
        @Test
        void saveAndUpdate() {
            // given
            Member memberA = new Member(true, "uni1@naver.com", "afD23!", 1L, "uniA", Status.AWAY, 1L);
            Member memberB = new Member(true, "uni2@naver.com", "afD23!", 1L, "uniB", Status.AWAY, 1L);
            Member memberC = new Member(true, "uni3@naver.com", "afD23!", 1L, "uniC", Status.AWAY, 1L);

            memberRepository.save(memberA);
            memberRepository.save(memberB);
            memberRepository.save(memberC);

            RiotAccount riotAccountA = RiotAccount.builder()
                .member(memberA)
                .frequentChampionId1(3L)
                .build();
            RiotAccount riotAccountB = RiotAccount.builder()
                .member(memberB)
                .frequentChampionId1(3L)
                .build();
            RiotAccount riotAccountC = RiotAccount.builder()
                .member(memberC)
                .frequentChampionId1(3L)
                .build();
            List<RiotAccount> riotAccounts = new ArrayList<>(Arrays.asList(riotAccountA, riotAccountB, riotAccountC));

            User userA = createUser("userA", memberA.getId());
            User userB = createUser("userB", memberB.getId());
            userRepository.save(userA);
            userRepository.save(userB);

            // when
            stompService.createOrUpdateUser(riotAccounts);

            // then
            Optional<User> findUserA = userRepository.findByActualUserId(memberA.getId());
            assertThat(findUserA).isPresent();
            assertThat(findUserA.get().getFrequentChampionId1()).isEqualTo(3L);
            assertThat(findUserA.get().getNowStatus()).isEqualTo(Status.ONLINE);

            Optional<User> findUserB = userRepository.findByActualUserId(memberB.getId());
            assertThat(findUserB).isPresent();
            assertThat(findUserB.get().getFrequentChampionId1()).isEqualTo(3L);

            Optional<User> findUserC = userRepository.findByActualUserId(memberC.getId());
            assertThat(findUserC).isPresent();
            assertThat(findUserC.get().getFrequentChampionId1()).isEqualTo(3L);
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

