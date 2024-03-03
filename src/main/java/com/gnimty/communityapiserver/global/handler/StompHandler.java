package com.gnimty.communityapiserver.global.handler;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.connect.WebSocketSessionManager;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.Map;
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

		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		// websocket 연결시 헤더의 jwt token 유효성 검증
		if (StompCommand.CONNECT == accessor.getCommand()) {
			Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
			assert sessionAttributes != null;
			Long memberId = Long.valueOf((String) sessionAttributes.get("sessionId"));
			log.info("memberId: {}", memberId);
			webSocketSessionManager.addSession(accessor.getSessionId(), memberId);
		}

		else {
			log.info(StompLog.builder()
				.command(accessor.getCommand().toString())
				.destination(accessor.getDestination())
				.sessionId(accessor.getSessionId())
				.build().toString());
		}
		return message;
	}

	private String parseTokenByHeader(StompHeaderAccessor accessor) {
		String token = jwtProvider.extractJwt(accessor);
		if (token == null) {
			throw new BaseException(ErrorCode.COOKIE_NOT_FOUND);
		}
		token = token.substring(token.indexOf("accessToken=") + "accessToken=".length());
		int semicolonIndex = token.indexOf(';') == -1 ? token.length() : token.indexOf(';');
		token = token.substring(0, semicolonIndex);
		return token;
	}
}