package com.gnimty.communityapiserver.global.handler;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.connect.WebSocketSessionManager;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@RequiredArgsConstructor
@Slf4j
@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtProvider jwtProvider;
	private final WebSocketSessionManager webSocketSessionManager;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		Arrays.stream(((HttpServletRequest) request).getCookies())
			.forEach(cookie -> log.info("cookie: {}, {}", cookie.getName(), cookie.getValue()));
		Optional<String> tokenByCookie = jwtProvider.resolveToken(httpRequest);
		if (tokenByCookie.isEmpty()) {
			return false;
		}

		String token = tokenByCookie.get();
		jwtProvider.checkValidation(token);
		Member member = jwtProvider.findMemberByToken(token);
		webSocketSessionManager.addSession(httpRequest.getSession().getId(), member.getId());
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {
	}
}
