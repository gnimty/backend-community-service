package com.gnimty.communityapiserver.domain.chat.controller;

import com.gnimty.communityapiserver.domain.block.service.BlockReadService;
import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageRequest;
import com.gnimty.communityapiserver.domain.chat.controller.dto.MessageResponse;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.service.ChatRoomService;
import com.gnimty.communityapiserver.domain.chat.service.StompService;
import com.gnimty.communityapiserver.domain.chat.service.UserService;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.domain.member.service.dto.request.StatusUpdateServiceRequest;
import com.gnimty.communityapiserver.global.connect.WebSocketSessionManager;
import com.gnimty.communityapiserver.global.constant.MessageRequestType;
import com.gnimty.communityapiserver.global.constant.MessageResponseType;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
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

	private final StompService chatService;
	private final ChatRoomService chatRoomService;
	private final MemberService memberService;
	private final UserService userService;
	private final BlockReadService blockReadService;
	private final WebSocketSessionManager webSocketSessionManager;

	// 채팅의 모든 조회
	@SubscribeMapping("/init_chat")
	public List<ChatRoomDto> getTotalChatRoomsAndChatsAndOtherUserInfo(@Header("simpSessionId") String sessionId) {
		User user = getUserBySessionId(sessionId);
		return chatService.getChatRoomsJoined(user);
	}

	@SubscribeMapping("/enter_chatRoom/{chatRoomNo}")
	public void enterChatRoom(@DestinationVariable("chatRoomNo") final Long chatRoomNo,
								@Header("simpSessionId") String sessionId) {
		User user = getUserBySessionId(sessionId);
		chatService.accessChatRoom(user.getActualUserId(), chatRoomNo);
		chatService.readOtherChats(user, chatRoomNo);
	}

	@SubscribeMapping("/vacate_chatRoom/{chatRoomNo}")
	public void vacateChatRoom(@DestinationVariable("chatRoomNo") final Long chatRoomNo,
								@Header("simpSessionId") String sessionId) {
		User user = getUserBySessionId(sessionId);
		chatService.releaseChatRoom(user.getActualUserId(), chatRoomNo);
	}

	// 채팅방 구독 유도
	// 이미 채팅방이 존재한다면 채팅방 정보만 넘겨주고 상대방 구독유도는 하지 않기 -> Front 1차 처리, Back 2차 처리
	@MessageMapping("/user/{otherUserId}")
	public void createChatRoomAndDerive(@DestinationVariable("otherUserId") Long otherUserId,
										@Header("simpSessionId") String sessionId) {

		User me = getUserBySessionId(sessionId);
		User other = userService.getUser(otherUserId);

		Boolean isMeBlock = blockReadService.existsByBlockerIdAndBlockedId(me.getActualUserId(), other.getActualUserId());
		Boolean isOtherBlock = blockReadService.existsByBlockerIdAndBlockedId(other.getActualUserId(), me.getActualUserId());

		ChatRoomDto chatRoomDto = chatService.getOrCreateChatRoomDto(
			new UserWithBlockDto(me, isMeBlock.equals(true) ? Blocked.BLOCK : Blocked.UNBLOCK),
			new UserWithBlockDto(other, isOtherBlock.equals(true) ? Blocked.BLOCK : Blocked.UNBLOCK)
		);

		// getchatRoomNo를 호출하기 X
		// chatRoom을 먼저 생성 또는 조회 후 그 정보를 그대로 보내주거나 DTO로 변환해서 보내주는 게 좋아 보임
		chatService.sendToUserSubscribers(me.getId(), new MessageResponse(MessageResponseType.CHATROOMINFO, chatRoomDto));

		if (!isOtherBlock)
		{
			chatService.sendToUserSubscribers(other.getId(), new MessageResponse(
				MessageResponseType.CHATROOMINFO, chatRoomDto));
		}
	}


	@MessageMapping("/chatRoom/{chatRoomNo}")
	public void sendMessage(@DestinationVariable("chatRoomNo") Long chatRoomNo,
							@Header("simpSessionId") String sessionId,
							final @Valid MessageRequest request) {
		User user = getUserBySessionId(sessionId);

		if (request.getType() == MessageRequestType.CHAT) {
			chatService.sendChat(user, chatRoomNo, request);
			chatService.sendToChatRoomSubscribers(chatRoomNo, new MessageResponse(MessageResponseType.CHATMESSAGE, request.getData()));
		} else {
			chatService.exitChatRoom(user, chatRoomService.getChatRoom(chatRoomNo));
		}
	}


	@EventListener
	public void onClientDisconnect(SessionDisconnectEvent event) {
		User user = getUserBySessionId(event.getSessionId());
		if (!isMultipleUser(user.getActualUserId())) {
			chatService.updateConnStatus(user, Status.OFFLINE);
			memberService.updateStatus(user.getActualUserId(), StatusUpdateServiceRequest.builder().status(Status.OFFLINE).build());
		}
		chatService.releaseChatRoomByUserId(user.getActualUserId());
		webSocketSessionManager.deleteSession(event.getSessionId());
	}

	@EventListener
	public void onClientConnect(SessionConnectedEvent event) {
		String sessionId = String.valueOf(event.getMessage().getHeaders().get("simpSessionId"));
		User user = getUserBySessionId(sessionId);
		if (!isMultipleUser(user.getActualUserId())) {
			chatService.updateConnStatus(user, Status.ONLINE);
			memberService.updateStatus(user.getActualUserId(), StatusUpdateServiceRequest.builder().status(Status.ONLINE).build());
		}
	}

	private User getUserBySessionId(String sessionId) {
		Long memberId = webSocketSessionManager.getMemberId(sessionId);
		return userService.getUser(memberId);
	}

	private boolean isMultipleUser(long memberId) {
		return webSocketSessionManager.getSessionCountByMemberId(memberId) > 1;
	}
}
