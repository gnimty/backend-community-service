package com.gnimty.communityapiserver.global.config;

import com.gnimty.communityapiserver.global.interceptor.HttpHandshakeInterceptor;
import com.gnimty.communityapiserver.global.handler.StompExceptionHandler;
import com.gnimty.communityapiserver.global.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompConfiguration implements WebSocketMessageBrokerConfigurer {

	private final StompHandler stompHandler;
	private final StompExceptionHandler stompExceptionHandler;
	private final HttpHandshakeInterceptor httpHandshakeInterceptor;

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		registry.enableSimpleBroker("/sub"); //클라이언트로 메세지를 응답 해 줄 때 prefix 정의 - 클라이언트가 메세지를 받을 때
		registry.setApplicationDestinationPrefixes("/pub", "/sub"); //클라이언트에서 메세지 송신 시 붙일 prefix 정의 - 클라이언트가 메세지를 보낼때
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry
			.setErrorHandler(stompExceptionHandler)
			.addEndpoint("/chat")
			//.addInterceptors((HandshakeInterceptor) stompHandler)

			.setAllowedOriginPatterns("*")
			.addInterceptors(httpHandshakeInterceptor);

		// SockJS 사용 시
		registry
			.setErrorHandler(stompExceptionHandler)
			.addEndpoint("/chat")
			//.addInterceptors((HandshakeInterceptor) stompHandler)

			.setAllowedOriginPatterns("*")
			.addInterceptors(httpHandshakeInterceptor)
			.withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompHandler);
	}
}
