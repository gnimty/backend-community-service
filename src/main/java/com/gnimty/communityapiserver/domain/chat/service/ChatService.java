package com.gnimty.communityapiserver.domain.chat.service;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageResponse;
import com.gnimty.communityapiserver.domain.chat.controller.dto.UserDto;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.Chat;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.MessageType;
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
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SeqGeneratorService generator;
	private final SimpMessagingTemplate template;

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
    public ChatRoomDto getOrCreateChatRoom(UserWithBlockDto me, UserWithBlockDto other) {
        Optional<ChatRoom> nullableChatRoom = chatRoomRepository.findByUsers(me.getUser(), other.getUser());
		ChatRoom chatRoom = nullableChatRoom.orElseGet(() ->
			chatRoomRepository.save(me, other, generator.generateSequence(ChatRoom.SEQUENCE_NAME))
		);

		return ChatRoomDto.builder()
			.chatRoom(chatRoom)
			.other(new UserDto(other.getUser()))
			.build();
    }

    // TODO solomon: 채팅방 목록 불러오기
    // blocked==UNBLOCK인 도큐먼트만 조회
    public List<ChatRoomDto> getChatRoomsJoined(User me) {

        List<ChatRoom> chatRooms = chatRoomRepository.findByUser(me)
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
        Long chatRoomNo = chatRoom.getChatRoomNo();
        Participant participant = extractParticipant(me, chatRoom.getParticipants(), false);

        // chatRoom lastModifiedDate, 상대방의 exitDate 비교
        if (participant.getExitDate() == null
            || chatRoom.getLastModifiedDate().getTime() < participant.getExitDate().getTime()) {
            // (상대방이 채팅방 나간 상황) lastModifiedDate가 상대의 exitDate 이전일 때 : flush
            //      -> flushAllChats() + chatRoomRepository.deleteByChatRoomNo()
            flushAllChats(chatRoomNo);
            chatRoomRepository.deleteByChatRoomNo(chatRoomNo);
        } else {
            // (상대방이 채팅방 나가지 않은 상황) lastModifiedDate가 상대의 exitDate 이후일 때 : exitDate update
            //      -> chatRoomRepository.updateExitDate(me);
            participant.setExitDate(new Date());
            chatRoomRepository.save(chatRoom);
        }
    }

    // TODO solomon : 단일 채팅방 정보 가져오기
    public ChatRoom getChatRoom(Long chatRoomNo) {
        return chatRoomRepository.findByChatRoomNo(chatRoomNo).orElseThrow(
            () -> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM,
                String.format(ErrorCode.NOT_FOUND_CHAT_ROOM.getMessage(), chatRoomNo)));
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
    public void createOrUpdateUser(RiotAccount riotAccount) {
		User user = userRepository.save(User.toUser(riotAccount));

		List<ChatRoom> chatRooms = chatRoomRepository.findByUser(user);
		MessageResponse response = new MessageResponse(MessageType.USERINFO, new UserDto(user));

		chatRooms.forEach(chatRoom -> sendToChatRoomSubscribers(chatRoom.getChatRoomNo(), response));
    }

    // 차단
    public void updateBlockStatus(Long meActualId, Long otherActualId, Blocked status) {
        // 1. 나와 상대가 속해 있는 채팅방을 찾기 (수정예정)
        try{
			User me = getUser(meActualId);
			User other = getUser(otherActualId);
			ChatRoom chatRoom = chatRoomRepository.findByUsers(me, other)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM));

            // 2. 해당 채팅방의 participant 정보 수정 후 save
            extractParticipant(me, chatRoom.getParticipants(), true).setBlockedStatus(status);

            chatRoomRepository.save(chatRoom);

            if (status == Blocked.BLOCK) {
                exitChatRoom(me, chatRoom);
            }

        }catch(BaseException e){
            log.info("두 유저가 존재하는 채팅방이 존재하지 않습니다.");
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
		List<Chat> totalChats = chatRepository.findByChatRoomNo(chatRoomNo);
		Date exitDate = getExitDateChatRoom(chatRoomNo, me.getId());

		return getChatDtoAfterExitDate(totalChats, exitDate);
	}


	// TODO janguni: 채팅 저장
	public void saveChat(User user, Long chatRoomNo, String message) {
		Date now = new Date();

		Chat chat = Chat.builder()
			.senderId(user.getActualUserId())
			.chatRoomNo(chatRoomNo)
			.message(message)
			.sendDate(now)
			.readCnt(1)
			.build();
		ChatRoom chatRoom = getChatRoom(chatRoomNo);

		chatRoom.setLastModifiedDate(now);
		chatRoomRepository.save(chatRoom);
		chatRepository.save(chat);
	}

	// TODO janguni: 접속정보 변동내역 전송
	public void updateConnStatus(User user, Status connectStatus) {
		user.setStatus(connectStatus);
		userRepository.save(user);

		List<ChatRoom> chatRooms = chatRoomRepository.findByUser(user);
		MessageResponse response = new MessageResponse(MessageType.CONNECTSTATUS, connectStatus);

		chatRooms.forEach(chatRoom -> sendToChatRoomSubscribers(chatRoom.getChatRoomNo(), response));
	}

	// TODO januni: 채팅방의 모든 채팅내역 Flush
	// 두명 다 나간 상황일 때만 호출
	public void flushAllChats(Long chatRoomNo) {
		chatRepository.deleteByChatRoomNo(chatRoomNo);
	}

	// TODO janguni: 채팅방에 있는 상대방이 보낸 채팅의 readCount update
	public void checkChatsInChatRoom(User me, Long chatRoomNo) {
		ChatRoom chatRoom = getChatRoom(chatRoomNo);

		Long otherActualUserId = getOther(me, chatRoom).getActualUserId();

		List<Chat> totalChats = chatRepository.findByChatRoomNo(chatRoomNo);
		totalChats.stream()
			.filter(chat -> (chat.getReadCnt() == 1 && chat.getSenderId().equals(otherActualUserId)))
			.forEach(chat -> {
				chat.setReadCnt(0);
				chatRepository.save(chat);
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
