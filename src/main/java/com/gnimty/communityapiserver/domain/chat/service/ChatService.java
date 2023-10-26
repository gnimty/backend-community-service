package com.gnimty.communityapiserver.domain.chat.service;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomInfo;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.chat.service.dto.ChatDto;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.auth.WebSocketSessionManager;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

	private final ChatRoomRepository chatRoomRepository;
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final MemberRepository memberRepository;
	private final SeqGeneratorService generator;
	private final WebSocketSessionManager sessionManager;

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
	public ChatRoom getOrCreateChatRoom(User me, User other) {
		Optional<ChatRoom> chatRoom = chatRoomRepository.findByUsers(me, other);

		if (chatRoom.isPresent()) {
			return chatRoom.get();
		} else {
			return chatRoomRepository.save(me, other,
				generator.generateSequence(ChatRoom.SEQUENCE_NAME));
		}
	}

	// TODO solomon: 채팅방 목록 불러오기
	// blocked==UNBLOCK인 도큐먼트만 조회
	public List<ChatRoomInfo> getChatRoomsJoined(User me) {

		List<ChatRoom> chatRooms = chatRoomRepository.findByUser(me)
			.stream().filter(chatRoom ->
				extractParticipant(me, chatRoom.getParticipants(), true).getBlockedStatus()
					== Blocked.UNBLOCK
			).toList();

		for (ChatRoom chatRoom : chatRooms) {
			//List<Object> chatList = getChatList(me, chatRoom.getChatRoomNo());
			// 윤희님 작업 끝나면 DTO에 추가 예정
		}

		return chatRooms.stream().map(chatRoom ->
			new ChatRoomInfo(chatRoom.getChatRoomNo(),
				extractParticipant(me, chatRoom.getParticipants(), false).getUser()
					.getActualUserId())
		).toList();
	}

	// TODO solomon: 채팅방 나가기 (채팅방에 기록된 내 나간시간 기록 update)
	// 양쪽 다 채팅방을 나간 상황이면, 모든 채팅 기록 삭제
	public void exitChatRoom(User me, Long chatRoomNo) {
		// 채팅방 확인
		ChatRoom chatRoom = getChatRoom(chatRoomNo).orElseThrow(
			() -> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM,
				String.format(ErrorCode.NOT_FOUND_CHAT_ROOM.getMessage(), chatRoomNo)));

		Participant participant = extractParticipant(me, chatRoom.getParticipants(), false);

		// chatRoom lastModifiedDate, 상대방의 exitDate 비교
		if(chatRoom.getLastModifiedDate().getTime() < participant.getExitDate().getTime()){
			// (상대방이 채팅방 나간 상황) lastModifiedDate가 상대의 exitDate 이전일 때 : flush
			//      -> flushAllChats() + chatRoomRepository.deleteByChatRoomNo()
			flushAllChats(chatRoomNo);
			chatRoomRepository.deleteByChatRoomNo(chatRoomNo);
		}else{
			// (상대방이 채팅방 나가지 않은 상황) lastModifiedDate가 상대의 exitDate 이후일 때 : exitDate update
			//      -> chatRoomRepository.updateExitDate(me);
			participant.setExitDate(new Date());
			chatRoomRepository.save(chatRoom);
		}
	}

	// TODO solomon : 단일 채팅방 정보 가져오기
	public Optional<ChatRoom> getChatRoom(Long chatRoomNo) {
		return chatRoomRepository.findByChatRoomNo(chatRoomNo);
	}

	// TODO solomon : 유저정보 가져오기
	public User getUser(Long actualUserId) throws BaseException {
		Optional<User> user = userRepository.findByActualUserId(actualUserId);

		if (user.isPresent()) {
			return user.get();
		} else {
			throw new BaseException(ErrorCode.NOT_FOUND_CHAT_USER);
		}
	}



	// TODO solomon : 유저정보 생성하기
	public User createOrUpdateUser(RiotAccount riotAccount)  {
		Optional<User> user = userRepository.findByActualUserId(
			riotAccount.getMember().getId());
		if(user.isPresent()){
			return user.get();
		}else{
			return userRepository.save(User.toUser(riotAccount));
		}
	}

	// 차단 시 chatRoom의 Participant 차단상태 변경
	public void updateBlockedStatus(User me, User other, Blocked blockedStatus){
		// 1. 나와 상대가 속해 있는 채팅방을 찾기
		ChatRoom chatRoom = chatRoomRepository.findByUsers(me, other).orElseThrow(()-> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM));

		// 2. 해당 채팅방의 participant 정보 수정 후 save
		extractParticipant(me, chatRoom.getParticipants(), true).setBlockedStatus(blockedStatus);

		chatRoomRepository.save(chatRoom);
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
		List<Chat> totalChats = chatRepository.findBySenderIdAndChatRoomNo(me.getId(), chatRoomNo);
		Date exitDate = getExitDateChatRoom(chatRoomNo, me.getId());

		return getChatDtoAfterExitDate(totalChats, exitDate);
	}


	// TODO janguni: 채팅 저장
	// chat 생성시 id 전략 필요
	public void saveChat(User user, Long chatRoomNo, String message) {
		//Long senderId = 1L;
		//Chat chat = new Chat("id", chatRoomNo, 1L, (String) message, new Date(), 1);
		//chatRepository.save(chat);
	}

	// TODO janguni: 접속정보 변동내역 전송
	public Object updateStatus(User user, Status connectStatus) {
		// userRepository.updateStatus()
		return null;
	}

	// TODO januni: 채팅방의 모든 채팅내역 Flush
	// 두명 다 나간 상황일 때만 호출
	public void flushAllChats(Long chatRoomNo) {
		chatRepository.deleteByChatRoomNo(chatRoomNo);
	}

	// TODO janguni: 채팅방에 있는 모든 채팅의 readCount update
	// 채팅 readCount update 필요
	public void checkChatsInChatRoom(User me, Long chatRoomId) {
		// update All chats by chatRoomId
		List<Chat> totalChats = chatRepository.findByChatRoomNo(chatRoomId);
		for (Chat c : totalChats) {
			//if (c.getReadCnt() == 1) chatRepository.updateReadCountById(c.getId(), 0);
		}
	}


	private static List<ChatDto> getChatDtoAfterExitDate(List<Chat> totalChats, Date exitDate) {
		List<ChatDto> chatDtos = new ArrayList<>();
		for (Chat c : totalChats) {
			if (c.getSendDate().before(exitDate)) break;

			chatDtos.add(
				ChatDto.builder()
					.senderId(c.getSenderId())
					.sendDate(c.getSendDate())
					.message(c.getMessage())
					.readCount(c.getReadCnt())
					.build());
		}
		return chatDtos;
	}

	private Date getExitDateChatRoom(Long chatRoomNo, String senderId) {
		ChatRoom findChatRoom = chatRoomRepository.findByChatRoomNo(chatRoomNo).get();
		List<Participant> participants = findChatRoom.getParticipants();

		for (Participant participant : participants) {
			if (participant.getUser().getId().equals(senderId)) {
				return participant.getExitDate();
			}
		}

		throw new BaseException(ErrorCode.NOT_FOUND_CHAT_USER);

	}


	/**isMe==true이면 Participant List에서 내 Participant 정보를 추출
	 * isMe==false이면 other user에 해당하는 Participant 정보를 추출
	 * @param me 자신의 유저정보
	 * @param participants 추출할 대상이 되는 Participant
	 * @param isMe 자신의 유저정보를 가져올 것인지 여부
	 * @return Participant
	 */
	public Participant extractParticipant(User me, List<Participant> participants, Boolean isMe) {
		return participants.get(0).getUser().equals(me) ^ isMe ?
			participants.get(1) : participants.get(0);
	}

}
