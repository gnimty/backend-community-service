package com.gnimty.communityapiserver.domain.chat.service;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageRequest;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageResponse;
import com.gnimty.communityapiserver.domain.chat.controller.dto.UserConnStatusDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.UserDto;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.MessageResponseType;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.mongodb.bulk.BulkWriteResult;
import java.util.Date;
import java.util.List;
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

        List<ChatRoom> chatRooms = chatRoomService.findChatRoom(me)
            .stream().filter(chatRoom ->
                extractParticipant(me, chatRoom.getParticipants(), true).getBlockedStatus()
                    == Blocked.UNBLOCK
            ).toList();

        return chatRooms.stream().map(chatRoom ->
            ChatRoomDto.builder()
                .chats(getChatList(me, chatRoom.getChatRoomNo()))
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
		if (other.getExitDate() != null
			&& chatRoom.getLastModifiedDate().before(other.getExitDate())) {

			Long chatRoomNo = chatRoom.getChatRoomNo();

			chatService.delete(chatRoomNo);
			chatRoomService.delete(chatRoomNo);
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
			MessageResponse response = new MessageResponse(MessageResponseType.USER_INFO, new UserDto(user));
			chatRooms.forEach(
				chatRoom -> sendToChatRoomSubscribers(chatRoom.getChatRoomNo(), response));
		}
    }


	public void destroyWithdrawnUserData(Long actualUserId) {
		User user = userService.getUser(actualUserId);
		userService.delete(user);

		chatRoomService.findChatRoom(user)
			.forEach(chatRoom -> {
				chatRoomService.delete(chatRoom.getChatRoomNo());
				chatService.delete(chatRoom.getChatRoomNo());
				sendToChatRoomSubscribers(chatRoom.getChatRoomNo(), new MessageResponse(MessageResponseType.DELETED_CHATROOM, chatRoom.getId()));
			});
	}


	public void createOrUpdateUser(List<RiotAccount> accounts){
		List<User> users = accounts.stream().map(account-> User.toUser(account)).toList();

		BulkWriteResult bulkWriteResult = userService.updateMany(users);
	}

	// TODO so1omon : 특정 유저와 채팅을 나눈 member id list 넘기기
	public List<Long> getChattedMemberIds(Long id){

		// 0. 유저 정보 검색
		User me = userService.getUser(id);

		// 1. 내 정보로 chatRoom 리스트 검색
		List<ChatRoom> chatRooms = chatRoomService.findChatRoom(me);

		// 2. chatRoom에 속해 있는 모든 other participants 정보 검색하여 Id 추출
		List<Long> memberIds = chatRooms.stream().map(chatRoom ->
			 getOther(me, chatRoom).getActualUserId()).toList();

		// 3. return
		return memberIds;
	}


    // 차단
    public void updateBlockStatus(Long myActualId, Long otherActualId, Blocked status) {
        // 1. 나와 상대가 속해 있는 채팅방을 찾기 (수정예정)
		User me = userService.getUser(myActualId);
		User other = userService.getUser(otherActualId);

		ChatRoom chatRoom = chatRoomService.findChatRoom(me, other)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM, "두 유저가 존재하는 채팅방이 존재하지 않습니다."));

		// 2. 해당 채팅방의 participant 정보 수정 후 save
		extractParticipant(me, chatRoom.getParticipants(), true).setBlockedStatus(status);

		chatRoomService.update(chatRoom);

		if (status == Blocked.BLOCK) {
			exitChatRoom(me, chatRoom);
		}
    }


   // TODO janguni : 유저가 차단했는지 확인
	public boolean isBlockParticipant(ChatRoom chatRoom, User other) {
		List<Participant> participants = chatRoom.getParticipants();
		Participant participant = extractParticipant(other, participants, true);
		return participant.getBlockedStatus().equals(Blocked.BLOCK);
	}


	// TODO janguni: 채팅방별 채팅 목록 불러오기 (exitDate < sendDate)
	public List<ChatDto> getChatList(User me, Long chatRoomNo) {

		// TODO: 시간 순서대로 오는건지 확인
		List<Chat> totalChats = chatService.findChat(chatRoomNo);
		Date exitDate = getExitDate(chatRoomNo, me);

		return getChatDtoAfterExitDate(totalChats, exitDate);
	}


	// TODO janguni: 채팅 저장
	public ChatDto sendChat(User user, Long chatRoomNo, MessageRequest request) {
		Date now = new Date();

		ChatRoom chatRoom = chatRoomService.findChatRoom(chatRoomNo)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM));

		Chat savedChat = chatService.save(user, chatRoomNo, request.getData(), now);

		chatRoom.setLastModifiedDate(now);
		chatRoomService.update(chatRoom);

		return new ChatDto(savedChat);
	}


	// TODO janguni: 접속정보 변동내역 전송
	public void updateConnStatus(User user, Status connectStatus) {
		user.setStatus(connectStatus);
		userService.save(user);

		List<ChatRoom> chatRooms = chatRoomService.findChatRoom(user);
		MessageResponse response = new MessageResponse(MessageResponseType.CONNECT_STATUS, new UserConnStatusDto(user.getActualUserId(), connectStatus));

		chatRooms.forEach(chatRoom ->
			sendToChatRoomSubscribers(chatRoom.getChatRoomNo(), response));
	}

	// TODO janguni: 채팅방에 있는 상대방이 보낸 채팅의 readCount update
	public void readOtherChats(User me, Long chatRoomNo) {
		ChatRoom chatRoom = chatRoomService.findChatRoom(chatRoomNo)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM));

		Long otherActualUserId = getOther(me, chatRoom).getActualUserId();

		List<Chat> totalChats = chatService.findChat(chatRoomNo);

		totalChats.stream()
			.filter(chat -> (chat.getReadCnt() == 1 && chat.getSenderId().equals(otherActualUserId)))
			.forEach(chat -> {
				chat.setReadCnt(0);
				chatService.save(chat);
			});
	}


	public void sendToUserSubscribers(String userId, MessageResponse response){
		template.convertAndSend("/sub/user/" + userId, response);
	}

	public void sendToChatRoomSubscribers(Long chatRoomId, MessageResponse response){
		template.convertAndSend("/sub/chatRoom/" + chatRoomId, response);
	}

	private User getOther(User me, ChatRoom chatRoom) {
		List<Participant> participants = chatRoom.getParticipants();
		Participant participant = extractParticipant(me, participants, false);
		User other = participant.getUser();
		return other;
	}


	private List<ChatDto> getChatDtoAfterExitDate(List<Chat> totalChats, Date exitDate) {
		return totalChats.stream()
			.filter(chat -> exitDate == null || chat.getSendDate().after(exitDate))
			.map(ChatDto::new)
			.toList();
	}


	private Date getExitDate(Long chatRoomNo, User user) {
		ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomNo);

		return extractParticipant(user,chatRoom.getParticipants(), true)
			.getExitDate();
	}


	/**isMe==true이면 Participant List에서 내 Participant 정보를 추출
	 * isMe==false이면 other user에 해당하는 Participant 정보를 추출
	 * @param me 자신의 유저정보
	 * @param participants 추출할 대상이 되는 Participant
	 * @param isMe 자신의 유저정보를 가져올 것인지 여부
	 * @return Participant
	 */
	public Participant extractParticipant(User me, List<Participant> participants, Boolean isMe) {
		int stdUserFoundedCnt = 0;
		int stdUserIdx = 0;

		//1. 나를 기준으로 participant 찾기
		for(int i=0;i<2;i++){
			Participant participant =participants.get(i);
			if(participant.getUser().getActualUserId().equals(me.getActualUserId())){
				stdUserFoundedCnt++;
				stdUserIdx = i;
			}
		}

		//2. 만약 내 유저 정보가 없다면 오류 반환
		if(stdUserFoundedCnt!=2){
			throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, "Participants가 유효하지 않습니다.");
		}

		//3. isMe에 따라 return하기
		return isMe ? participants.get(stdUserIdx) : participants.get(1-stdUserIdx);
	}



}
