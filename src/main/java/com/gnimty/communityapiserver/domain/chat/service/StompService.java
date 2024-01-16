package com.gnimty.communityapiserver.domain.chat.service;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageResponse;
import com.gnimty.communityapiserver.domain.chat.controller.dto.UserConnStatusDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.UserDto;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.MessageResponseType;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StompService {

	private final SeqGeneratorService generator;
	private final SimpMessagingTemplate template;
	private final UserService userService;
	private final ChatService chatService;
	private final ChatRoomService chatRoomService;

    /*
    TODO 리스트
    채팅방 생성 또는 조회
    채팅방 목록 불러오기 (status:UNBLOCK)
    채팅방 나가기 (채팅방에 기록된 내 나간시간 기록 update)
    채팅방별 채팅 목록 불러오기 (exitDate < sendDate)
    유저정보 조회
    채팅방에서 유저가 차단한 상태인지 확인
    채팅 보내기
    접속정보 변동내역 전송
    채팅방의 모든 채팅내역 Flush
    채팅 readCount check
    */

	// TODO solomon: 채팅방 생성 또는 조회
	// 이미 차단정보 확인된 상황
	public ChatRoomDto getOrCreateChatRoomDto(UserWithBlockDto me, UserWithBlockDto other) {

		if (me.getStatus() == Blocked.BLOCK && other.getStatus() == Blocked.BLOCK) {
			throw new BaseException(ErrorCode.NOT_ALLOWED_CREATE_CHAT_ROOM);
		}

		ChatRoom chatRoom = chatRoomService.findChatRoom(me.getUser(), other.getUser())
			.orElseGet(() -> chatRoomService.save(me, other));

		return ChatRoomDto.builder()
			.chatRoom(chatRoom)
			.other(new UserDto(other.getUser()))
			.build();
	}


	// TODO solomon: 채팅방 목록 불러오기
	// blocked==UNBLOCK인 도큐먼트만 조회
	public List<ChatRoomDto> getChatRoomsJoined(User me) {

		List<ChatRoom> chatRooms = chatRoomService.findUnBlockChatRoom(me);

		return chatRooms.stream().map(chatRoom ->
			ChatRoomDto.builder()
				.chats(getChatList(me, chatRoom))
				.chatRoom(chatRoom)
				.other(new UserDto(
					extractParticipant(me, chatRoom.getParticipants(), false).getUser()))
				.build()
		).toList();
	}


	// TODO solomon: 채팅방 나가기 (채팅방에 기록된 내 나간시간 기록 update)
	// 양쪽 다 채팅방을 나간 상황이면, 모든 채팅 기록 삭제
	public void exitChatRoom(User me, ChatRoom chatRoom) {
		Participant other = extractParticipant(me, chatRoom.getParticipants(), false);
		Participant mine = extractParticipant(me, chatRoom.getParticipants(), true);
		// chatRoom lastModifiedDate, 상대방의 exitDate 비교

		// (상대방이 채팅방 나간 상황) lastModifiedDate가 상대의 exitDate 이전일 때 : flush
		//      -> flushAllChats() + chatRoomRepository.deleteByChatRoomNo()
		if ((other.getExitDate() != null && chatRoom.getLastModifiedDate()
			.before(other.getExitDate()))
			|| other.getBlockedStatus() == Blocked.BLOCK) {
			destroyChatRoomAndChat(chatRoom);
		}
		// (상대방이 채팅방 나가지 않은 상황) lastModifiedDate가 상대의 exitDate 이후일 때 : exitDate update
		//      -> chatRoomRepository.updateExitDate(me);
		else {
			mine.setExitDate(new Date());
			chatRoomService.update(chatRoom);
		}
	}

	// TODO solomon : 유저정보 생성하기
	public void createOrUpdateUser(RiotAccount riotAccount) {
		User user = userService.save(riotAccount);

		List<ChatRoom> chatRooms = chatRoomService.findChatRoom(user);
		if (!chatRooms.isEmpty()) {
			MessageResponse response = new MessageResponse(MessageResponseType.USER_INFO,
				new UserDto(user));
			chatRooms.forEach(
				chatRoom -> sendToChatRoomSubscribers(chatRoom.getChatRoomNo(), response));
		}
	}


	public void withdrawal(Long actualUserId) {
		Optional<User> findUser = userService.findUser(actualUserId);
		if (findUser.isPresent()) {
			userService.delete(findUser.get());
			chatRoomService.findChatRoom(findUser.get())
				.forEach(chatRoom -> {
					destroyChatRoomAndChat(chatRoom);
					sendToChatRoomSubscribers(chatRoom.getChatRoomNo(),
						new MessageResponse(MessageResponseType.DELETED_CHATROOM,
							chatRoom.getId()));
				});
		}
	}

	private void destroyChatRoomAndChat(ChatRoom chatRoom) {
		chatRoomService.delete(chatRoom);
		chatService.delete(chatRoom);
	}


	public void createOrUpdateUser(List<RiotAccount> accounts) {
		if (!accounts.isEmpty()) {
			accounts.stream()
				.filter(riotAccount ->
					userService.findUser(riotAccount.getMember().getId()).isEmpty())
				.forEach(userService::save);
			userService.updateMany(accounts.stream().map(User::toUser).toList());
		}
	}

	// TODO so1omon : 특정 유저와 채팅을 나눈 member id list 넘기기
	public List<Long> getChattedMemberIds(User user) {

		// 1. 내 정보로 chatRoom 리스트 검색
		List<ChatRoom> chatRooms = chatRoomService.findChatRoom(user);

		// 2. chatRoom에 속해 있는 모든 other participants 정보 검색하여 Id 추출
		// 3. return
		return chatRooms.stream().map(chatRoom ->
			getOther(user, chatRoom).getActualUserId()).toList();
	}

	public void updateBlockStatus(User me, User other, Blocked status) {
		Optional<ChatRoom> findChatRoom = chatRoomService.findChatRoom(me, other);
		if (findChatRoom.isPresent()) {
			extractParticipant(me, findChatRoom.get().getParticipants(), true).setBlockedStatus(
				status);
			chatRoomService.update(findChatRoom.get());
			if (status == Blocked.BLOCK) {
				exitChatRoom(me, findChatRoom.get());
			}
		}
	}


	// TODO janguni : 유저가 차단했는지 확인
	public boolean isBlockParticipant(ChatRoom chatRoom, User other) {
		List<Participant> participants = chatRoom.getParticipants();
		Participant participant = extractParticipant(other, participants, true);
		return participant.getBlockedStatus().equals(Blocked.BLOCK);
	}


	// TODO janguni: 채팅방별 채팅 목록 불러오기 (exitDate < sendDate)
	public List<ChatDto> getChatList(User me, ChatRoom chatRoom) {
		return chatService.findChats(chatRoom, getExitDate(chatRoom, me));
	}


	// TODO janguni: 채팅 저장
	public ChatDto saveChat(User user, ChatRoom chatRoom, String message) {
		Date now = new Date();

		Chat savedChat = chatService.save(user, chatRoom, message, now);

		chatRoom.refreshModifiedDate(now);
		chatRoomService.update(chatRoom);

		return new ChatDto(savedChat);
	}


	// TODO janguni: 접속정보 변동내역 전송
	public void updateConnStatus(User user, Status connectStatus, Boolean isByUser) {
		if (isByUser) {
			user.updateNowStatus(connectStatus);
			user.updateSelectedStatus(connectStatus);
		} else {
			user.updateNowStatus((connectStatus == Status.ONLINE) ? user.getSelectedStatus() : connectStatus);
		}
		userService.save(user);

		List<ChatRoom> chatRooms = chatRoomService.findChatRoom(user);
		MessageResponse response = new MessageResponse(MessageResponseType.CONNECT_STATUS,
			new UserConnStatusDto(user.getActualUserId(), connectStatus));

		chatRooms.forEach(chatRoom ->
			sendToChatRoomSubscribers(chatRoom.getChatRoomNo(), response));
	}

	// TODO janguni: 채팅방에 있는 상대방이 보낸 채팅의 readCount update
	public void readOtherChats(User me, ChatRoom chatRoom) {
		User other = getOther(me, chatRoom);
		chatService.readAllChat(chatRoom, other);
	}


	public void sendToUserSubscribers(Long userId, MessageResponse response) {
		template.convertAndSend("/sub/user/" + userId, response);
	}

	public void sendToChatRoomSubscribers(Long chatRoomId, MessageResponse response) {
		template.convertAndSend("/sub/chatRoom/" + chatRoomId, response);
	}


	private User getOther(User me, ChatRoom chatRoom) {
		List<Participant> participants = chatRoom.getParticipants();
		Participant participant = extractParticipant(me, participants, false);
		return participant.getUser();
	}

	private List<ChatDto> getChatDtoAfterExitDate(List<Chat> totalChats, Date exitDate) {
		return totalChats.stream()
			.filter(chat -> exitDate == null || chat.getSendDate().after(exitDate))
			.map(ChatDto::new)
			.toList();
	}

	private Date getExitDate(ChatRoom chatRoom, User user) {
		return extractParticipant(user, chatRoom.getParticipants(), true)
			.getExitDate();
	}


	/**
	 * isMe==true이면 Participant List에서 내 Participant 정보를 추출 isMe==false이면 other user에 해당하는 Participant 정보를 추출
	 *
	 * @param me           자신의 유저정보
	 * @param participants 추출할 대상이 되는 Participant
	 * @param isMe         자신의 유저정보를 가져올 것인지 여부
	 * @return Participant
	 */
	public Participant extractParticipant(User me, List<Participant> participants, Boolean isMe) {
		int stdUserFoundedCnt = 0;
		int stdUserIdx = 0;

		//1. 나를 기준으로 participant 찾기
		for (int i = 0; i < 2; i++) {
			Participant participant = participants.get(i);
			if (participant.getUser().getActualUserId().equals(me.getActualUserId())) {
				stdUserFoundedCnt++;
				stdUserIdx = i;
			}
		}

		//2. 만약 내 유저 정보가 없다면 오류 반환
		if (stdUserFoundedCnt != 1) {
			throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, "Participants가 유효하지 않습니다.");
		}

		//3. isMe에 따라 return하기
		return isMe ? participants.get(stdUserIdx) : participants.get(1 - stdUserIdx);
	}
}
