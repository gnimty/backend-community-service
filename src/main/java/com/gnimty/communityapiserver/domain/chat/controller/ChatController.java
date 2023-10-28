package com.gnimty.communityapiserver.domain.chat.controller;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomDto;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.service.ChatService;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.global.auth.WebSocketSessionManager;
import com.gnimty.communityapiserver.global.constant.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	private final ChatService chatService;
	private final MemberService memberService;
	private final SimpMessagingTemplate template;
	private final WebSocketSessionManager webSocketSessionManager;

	// 채팅의 모든 조회
	@SubscribeMapping("/init_chat")
	public List<ChatRoomInfo> getTotalChatRoomsAndChatsAndOtherUserInfo(@Header("simpSessionId") String sessionId) {
		User user = getUserBySessionId(sessionId);
		List<ChatRoomInfo> chatRoomInfos = chatService.getChatRoomsJoined(user);

		return chatRoomDtos;
	}

	// 채팅방 구독 유도
	// 이미 채팅방이 존재한다면 채팅방 정보만 넘겨주고 상대방 구독유도는 하지 않기 -> Front 1차 처리, Back 2차 처리
	@MessageMapping("/user/{otherUserId}")
	public void createChatRoomAndDerive(@DestinationVariable("otherUserId") Long otherUserId,
										@Header("simpSessionId") String sessionId) {

		User me = getUserBySessionId(sessionId);
		User other = chatService.getUser(otherUserId);

		// TODO janguni: 여기서 MemberService 호출해서 유저가 나를 차단했는지 정보를 가져오기
		ChatRoom chatRoom = chatService.getOrCreateChatRoom(
			new UserWithBlockDto(me, null),
			new UserWithBlockDto(other, null));

		// getchatRoomNo를 호출하기 X
		// chatRoom을 먼저 생성 또는 조회 후 그 정보를 그대로 보내주거나 DTO로 변환해서 보내주는 게 좋아 보임
		template.convertAndSend("/sub/user/" + me.getId(), chatRoom.getChatRoomNo());

		if (!chatService.isBlockParticipant(chatRoom, other)) //
		{
			template.convertAndSend("/sub/user/" + other.getId(), chatRoom.getChatRoomNo());
		}
	}

	// 메세지 전송
	@MessageMapping("/chatRoom/{chatRoomNo}")
	public void sendMessage(@DestinationVariable("chatRoomNo") Long chatRoomNo,
							@Header("simpSessionId") String sessionId,
							String message) {
		User user = getUserBySessionId(sessionId);
		chatService.saveChat(user, chatRoomNo, message);
		template.convertAndSend("/sub/chatRoom/" + chatRoomNo, message);
	}

	// 채팅방 나가기
	@SubscribeMapping("/chatRoom/exit/{chatRoomNo}")

	public void exitChatRoom(@DestinationVariable("chatRoomNo") Long chatRoomNo,
							 @Header("simpSessionId") String sessionId) {
		User user = getUserBySessionId(sessionId);
		chatService.exitChatRoom(user, chatRoomNo);
	}

	// chatRoomNo을 통한 sendStatus 필요
	@MessageMapping("/chatRoom/status/{chatRoomNo}")
	public void sendStatus(@DestinationVariable("chatRoomNo") Long chatRoomNo,
		  				   @Header("simpSessionId") String sessionId,
		                   String message) {
		template.convertAndSend("/sub/chatRoom/" + chatRoomNo, message);
	}

	@EventListener
	public void onClientDisconnect(SessionDisconnectEvent event) {
		User user = getUserBySessionId(event.getSessionId());
		if (!isMultipleUser(user.getActualUserId()))
			chatService.updateStatus(user, Status.OFFLINE);
		webSocketSessionManager.deleteSession(event.getSessionId());
	}

	@EventListener
	public void onClientConnect(SessionConnectedEvent event) {
		String sessionId = String.valueOf(event.getMessage().getHeaders().get("simpSessionId"));
		User user = getUserBySessionId(sessionId);
		if (!isMultipleUser(user.getActualUserId())) {
			chatService.updateStatus(user, Status.ONLINE);

		}
	}

	private User getUserBySessionId(String sessionId) {
		Long memberId = webSocketSessionManager.getMemberId(sessionId);
		User user = chatService.getUser(memberId);
		return user;
	}

	private boolean isMultipleUser(long memberId) {
		return webSocketSessionManager.getSessionCountByMemberId(memberId) > 1;
	}
}
