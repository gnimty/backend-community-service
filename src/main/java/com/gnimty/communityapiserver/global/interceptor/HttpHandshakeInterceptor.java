package com.gnimty.communityapiserver.global.interceptor;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@RequiredArgsConstructor
@Slf4j
@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtProvider jwtProvider;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest httpRequest) {
			HttpServletRequest servletRequest = httpRequest.getServletRequest();
			Optional<String> tokenByCookie = jwtProvider.resolveToken(servletRequest);
			if (tokenByCookie.isEmpty()) {
				throw new BaseException(ErrorCode.COOKIE_NOT_FOUND);
			}
			String token = tokenByCookie.get();
			jwtProvider.checkValidation(token);
			Member member = jwtProvider.findMemberByToken(token);
			attributes.put("memberId", member.getId());
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {
	}
}
