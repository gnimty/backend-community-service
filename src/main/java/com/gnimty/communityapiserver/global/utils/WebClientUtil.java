package com.gnimty.communityapiserver.global.utils;

import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebClientUtil {

	public static <T> T get(Class<T> t, String url, Consumer<HttpHeaders> headersConsumer) {
		WebClient.Builder webClientBuilder = WebClient.builder();

		if (headersConsumer != null) {
			webClientBuilder = webClientBuilder.defaultHeaders(headersConsumer);
		}

		return webClientBuilder.build()
			.get()
			.uri(url)
			.retrieve()
			.onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class)
				.flatMap(error -> Mono.error(new BaseException(ErrorCode.WEBCLIENT_CLIENT_ERROR, error))))
			.bodyToMono(t)
			.block();
	}

	public static <T> T post(
		Class<T> t,
		String url,
		MediaType mediaType,
		MultiValueMap<String, String> bodyMap,
		Consumer<HttpHeaders> headersConsumer
	) {
		WebClient.Builder webClientBuilder = WebClient.builder();

		if (headersConsumer != null) {
			webClientBuilder = webClientBuilder.defaultHeaders(headersConsumer);
		}

		return webClientBuilder.build()
			.post()
			.uri(url)
			.contentType(mediaType)
			.body(BodyInserters.fromFormData(bodyMap))
			.retrieve()
			.onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class)
				.flatMap(error -> Mono.error(new BaseException(ErrorCode.WEBCLIENT_CLIENT_ERROR, error))))
			.bodyToMono(t)
			.block();
	}
}
