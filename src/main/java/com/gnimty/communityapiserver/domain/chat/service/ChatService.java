package com.gnimty.communityapiserver.domain.chat.service;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomInfo;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.Status;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
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

		// chatRoomRepository.findByUsers()
		// chatRoomRepository.save()

		return null;
	}

	// TODO solomon: 채팅방 목록 불러오기
	// blocked==UNBLOCK인 도큐먼트만 조회
	public List<ChatRoomInfo> getChatRoomsJoined(User me) {

		// chatRoomRepository.findByUser()
		// getChatsByRoomNo()

		return null;
	}

	// TODO solomon: 채팅방 나가기 (채팅방에 기록된 내 나간시간 기록 update)
	// 양쪽 다 채팅방을 나간 상황이면, 모든 채팅 기록 삭제
	public void exitChatRoom(User me, Long chatRoomNo) {

		// chatRoom lastModifiedDate, 상대방의 exitDate 비교
		// lastModifiedDate < exitDate : flush
		//      -> flushAllChats() + chatRoomRepository.deleteByChatRoomNo();
		// lastModifiedDate < exitDate : exitDate update
		//      -> chatRoomRepository.updateExitDate(me);

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

	// TODO janguni : 유저가 차단했는지 확인
	// 유저가 차단 시 memberController 또는 memberService 안에서 업데이트 할 것
	// chatRoom entity 안에 정보가 이미 있기 떄문에, 서비스 레이어로 굳이 분리하지 않아도 되긴 함
	public boolean isBlock(ChatRoom chatRoom, User other) {
		// 자유 구현

		return false;
	}

	// TODO janguni: 채팅방별 채팅 목록 불러오기 (exitDate < sendDate)
	// cf) chatRoomId -> chatRoomNo로 통일해주세요! Object Id와 혼동이 올 수 있으실 것 같습니다. 필드도 구분되어있어용
	public List<Object> getChatList(User me, Long chatRoomNo) {
		// chatRepository.findBySenderIdAndChatRoomNo()

		return null;
	}

	// TODO janguni: 채팅 보내기
	public void sendChat(Long chatRoomNo, Object message) {
		// chatRepository.save()
	}

	// TODO janguni: 접속정보 변동내역 전송
	public Object updateStatus(User me, Status status) {
		// userRepository.updateStatus()

		return null;
	}

	// TODO januni: 채팅방의 모든 채팅내역 Flush
	// 두명 다 나간 상황일 때만 호출
	public void flushAllChats(Long chatRoomNo) {
		// chatRepository.deleteByChatRoomNo()
	}

	// TODO janguni: 채팅방에 있는 모든 채팅의 readCount update
	public void checkChatsInChatRoom(User me, Long chatRoomId) {
		// update All chats by chatRoomId
	}

}
