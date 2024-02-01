package com.gnimty.communityapiserver.global.utils;

import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebClientUtil {

	public static <T> T get(Class<T> t, String url, Consumer<HttpHeaders> headersConsumer) {
		return WebClient.create(url)
			.get()
			.headers(headersConsumer)
			.retrieve()
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
		return WebClient.create(url)
			.post()
			.headers(headersConsumer)
			.contentType(mediaType)
			.bodyValue(BodyInserters.fromFormData(bodyMap))
			.retrieve()
			.bodyToMono(t)
			.block();
	}
}
