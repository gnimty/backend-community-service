package com.gnimty.communityapiserver.global.handler;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.connect.WebSocketSessionManager;
import com.gnimty.communityapiserver.global.constant.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class StompHandler implements ChannelInterceptor {

	private final JwtProvider jwtProvider;
	private final WebSocketSessionManager webSocketSessionManager;


	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		log.info("StompHandler.preSend");
		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		// websocket 연결시 헤더의 jwt token 유효성 검증
		if (StompCommand.CONNECT == accessor.getCommand()) {
			final String authorization = jwtProvider.extractJwt(accessor);
			String token = authorization.replaceFirst(Auth.BEARER.getContent(), "");
			jwtProvider.checkValidation(token);

			Member member = jwtProvider.findMemberByToken(token);
			MemberThreadLocal.set(member);
			webSocketSessionManager.addSession(accessor.getSessionId(), member.getId());

		}
		return message;
	}


	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
		log.info("StompHandler. postSend");
		MemberThreadLocal.remove();
	}
}