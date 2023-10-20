package com.gnimty.communityapiserver.global.handler;

import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.auth.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

	private final JwtProvider jwtProvider;
	private final WebSocketSessionManager webSocketSessionManager;


	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		// websocket 연결시 헤더의 jwt token 유효성 검증
		if (StompCommand.CONNECT == accessor.getCommand()) {
			final String authorization = jwtProvider.extractJwt(accessor);

			// jwtProvider.checkValidation(authorization);

			// TODO: authorization으로 memberId 꺼내기
			// Long memberId = jwtProvider.findMemberByToken(authorization).getId();
			webSocketSessionManager.addSession(accessor.getSessionId(), Long.valueOf(authorization)); // 변경 예정
		}
		return message;
	}



}