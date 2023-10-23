package com.gnimty.communityapiserver.domain.chat.controller;

import com.gnimty.communityapiserver.domain.chat.controller.dto.ChatRoomInfo;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.Status;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.service.ChatService;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.global.auth.WebSocketSessionManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
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
	public List<ChatRoomInfo> getTotalChatRoomsAndChatsAndOtherUserInfo() {

		List<ChatRoomInfo> chatRoomInfos = chatService.getChatRoomsJoined(null);

		return chatRoomInfos;
	}

	// 채팅방 구독 유도
	// 이미 채팅방이 존재한다면 채팅방 정보만 넘겨주고 상대방 구독유도는 하지 않기 -> Front 1차 처리, Back 2차 처리
	@MessageMapping("/user/{otherUserId}")
	public void createChatRoomAndDerive(@DestinationVariable("otherUserId") Long otherUserId) {

		User me = null; // 내 정보 가져올 수 있으면 가져와서 여기에 넣기
		User other = chatService.getUser(otherUserId);

		// 여기서 MemberService 호출해서 유저가 나를 차단했는지 정보를 가져오기
		ChatRoom chatRoom = chatService.getOrCreateChatRoom(me, other);

		// getchatRoomNo를 호출하기 X
		// chatRoom을 먼저 생성 또는 조회 후 그 정보를 그대로 보내주거나 DTO로 변환해서 보내주는 게 좋아 보임

		Long myUserId = 2L; // 자신의 id (삭제 예정)
		template.convertAndSend("/sub/user/" + myUserId, chatRoom.getChatRoomNo());

		if (!chatService.isBlock(chatRoom, other)) //
		{
			template.convertAndSend("/sub/user/" + otherUserId, chatRoom.getChatRoomNo());
		}
	}

	// 메세지 전송
	@MessageMapping("/chatRoom/{chatRoomNo}")
	public void sendMessage(@DestinationVariable("chatRoomNo") Long chatRoomNo, String message) {
		chatService.saveChat(chatRoomNo, message);
		template.convertAndSend("/sub/chatRoom/" + chatRoomNo, message);
	}

	// 채팅방 나가기
	@SubscribeMapping("/chatRoom/exit/{chatRoomNo}")
	public void exitChatRoom(@DestinationVariable("chatRoomNo") Long chatRoomNo) {
		chatService.exitChatRoom(null, chatRoomNo);
	}

	// chatRoomNo을 통한 sendStatus 필요
	@MessageMapping("/chatRoom/status/{chatRoomNo}")
	public void sendStatus(@DestinationVariable("chatRoomNo") Long chatRoomNo, String message) {
		template.convertAndSend("/sub/chatRoom/" + chatRoomNo, message);
	}

	@EventListener
	public void onClientDisconnect(SessionDisconnectEvent event) {
		Long userId = webSocketSessionManager.disConnectSession(event.getSessionId());
		chatService.updateStatus(userId, Status.OFFLINE);
	}

	@EventListener
	public void onClientConnect(SessionConnectedEvent event) {
		String simpSessionId = String.valueOf(event.getMessage().getHeaders().get("simpSessionId"));
		Long userId = webSocketSessionManager.getMemberId(simpSessionId);
		chatService.updateStatus(userId, Status.ONLINE);
	}

}
