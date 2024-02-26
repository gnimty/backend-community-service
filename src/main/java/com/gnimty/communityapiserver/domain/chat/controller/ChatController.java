package com.gnimty.communityapiserver.domain.chat.controller;

import com.gnimty.communityapiserver.domain.block.service.BlockReadService;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageRequest;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageResponse;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.service.ChatRoomService;
import com.gnimty.communityapiserver.domain.chat.service.ChatService;
import com.gnimty.communityapiserver.domain.chat.service.StompService;
import com.gnimty.communityapiserver.domain.chat.service.UserService;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.global.connect.WebSocketSessionManager;
import com.gnimty.communityapiserver.global.constant.MessageRequestType;
import com.gnimty.communityapiserver.global.constant.MessageResponseType;
import com.gnimty.communityapiserver.global.constant.Status;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	private final StompService stompService;
	private final ChatService chatService;
	private final ChatRoomService chatRoomService;
	private final UserService userService;
	private final MemberService memberService;
	private final BlockReadService blockReadService;
	private final WebSocketSessionManager webSocketSessionManager;


	// 채팅의 모든 조회
	@SubscribeMapping("/init_chat")
	public List<ChatRoomDto> getTotalChatRoomsAndChatsAndOtherUserInfo(
		@Header("simpSessionId") String sessionId) {
		User user = getUserBySessionId(sessionId);
		return stompService.getChatRoomsJoined(user);
	}


	// 채팅방 구독 유도
	// 이미 채팅방이 존재한다면 채팅방 정보만 넘겨주고 상대방 구독유도는 하지 않기 -> Front 1차 처리, Back 2차 처리
	@MessageMapping("/user/{otherUserId}")
	public void createChatRoomAndDerive(@DestinationVariable("otherUserId") Long otherUserId,
		@Header("simpSessionId") String sessionId) {

		User me = getUserBySessionId(sessionId);
		User other = userService.getUser(otherUserId);

		Boolean isMeBlock = blockReadService.existsByBlockerIdAndBlockedId(me.getActualUserId(),
			other.getActualUserId());
		Boolean isOtherBlock = blockReadService.existsByBlockerIdAndBlockedId(
			other.getActualUserId(), me.getActualUserId());

		ChatRoomDto chatRoomDto = stompService.getOrCreateChatRoomDto(
			new UserWithBlockDto(me, isMeBlock.equals(true) ? Blocked.BLOCK : Blocked.UNBLOCK),
			new UserWithBlockDto(other, isOtherBlock.equals(true) ? Blocked.BLOCK : Blocked.UNBLOCK)
		);

		// getchatRoomNo를 호출하기 X
		// chatRoom을 먼저 생성 또는 조회 후 그 정보를 그대로 보내주거나 DTO로 변환해서 보내주는 게 좋아 보임
		stompService.sendToUserSubscribers(me.getActualUserId(),
			new MessageResponse(MessageResponseType.CHATROOM_INFO, chatRoomDto));

		if (!isOtherBlock) {
			stompService.sendToUserSubscribers(other.getActualUserId(), new MessageResponse(
				MessageResponseType.CHATROOM_INFO, chatRoomDto));
		}
	}


	@MessageMapping("/chatRoom/{chatRoomNo}")
	public void sendMessage(@DestinationVariable("chatRoomNo") Long chatRoomNo,
		@Header("simpSessionId") String sessionId,
		final @Valid MessageRequest request) {
		User user = getUserBySessionId(sessionId);
		ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomNo);

		if (request.getType() == MessageRequestType.CHAT) {
			ChatDto chatDto = stompService.saveChat(user, chatRoom, request.getData());
			stompService.sendToChatRoomSubscribers(chatRoomNo,
				new MessageResponse(MessageResponseType.CHAT_MESSAGE, chatDto));
		} else if (request.getType() == MessageRequestType.READ) {
			stompService.readOtherChats(user, chatRoom);
			stompService.sendToUserSubscribers(user.getActualUserId(),
				new MessageResponse(MessageResponseType.READ_CHATS, chatRoomNo));
		} else {
			stompService.exitChatRoom(user, chatRoomService.getChatRoom(chatRoomNo));
		}
	}


	@EventListener
	public void onClientConnect(SessionConnectedEvent event) {
		String sessionId = String.valueOf(event.getMessage().getHeaders().get("simpSessionId"));
		User user = getUserBySessionId(sessionId);
		log.info("[Connect] userId: {} ", user.getActualUserId());
		if (!isMultipleUser(user.getActualUserId())) {
			stompService.updateConnStatus(user, user.getSelectedStatus(), false);
			memberService.updateStatus(user.getSelectedStatus(), user.getActualUserId());
		}
	}

	@EventListener
	public void onClientDisconnect(SessionDisconnectEvent event) {
		User user = getUserBySessionId(event.getSessionId());
		log.info("[DisConnect] userId: {}", user.getActualUserId());
		if (!isMultipleUser(user.getActualUserId())) {
			stompService.updateConnStatus(user, Status.OFFLINE, false);
			memberService.updateStatus(Status.OFFLINE, user.getActualUserId());
		}
		webSocketSessionManager.deleteSession(event.getSessionId());
	}


	private User getUserBySessionId(String sessionId) {
		Long actualUserId = webSocketSessionManager.getMemberId(sessionId);
		return userService.getUser(actualUserId);
	}


	private boolean isMultipleUser(Long memberId) {
		int cnt = webSocketSessionManager.getSessionCountByMemberId(memberId);
		return webSocketSessionManager.getSessionCountByMemberId(memberId) > 1;
	}

}
