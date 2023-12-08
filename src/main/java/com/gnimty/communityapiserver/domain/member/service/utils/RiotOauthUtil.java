package com.gnimty.communityapiserver.domain.member.service.utils;


import static com.gnimty.communityapiserver.global.constant.Auth.BEARER;

import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class RiotOauthUtil {

	@Value("${oauth.riot.client_id}")
	private String client_id;
	@Value("${oauth.riot.client_secret}")
	private String client_secret;
	@Value("${oauth.riot.redirect_uri}")
	private String redirect_uri;
	private final JwtProvider jwtProvider;

	public String getPuuid(String authCode) {
		TokenInfo token;
		try {
			token = getTokenInfo(authCode);
		} catch (WebClientResponseException e) {
			throw new BaseException(ErrorCode.INVALID_AUTH_CODE);
		}
		return getAccountInfo(token).getPuuid();
	}

	private TokenInfo getTokenInfo(String authCode) {
		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("grant_type", "authorization_code");
		bodyMap.add("redirect_uri", redirect_uri);
		bodyMap.add("code", authCode);

		String header = "Basic " + client_id + ":" + client_secret;
//		Base64.getEncoder().encode(header.getBytes());

		return WebClient.create("https://auth.riotgames.com")
			.post()
			.uri("/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.header("Authorization", header)
			.body(BodyInserters.fromFormData(bodyMap))
			.retrieve()
			.bodyToMono(TokenInfo.class)
			.block();
	}

	private PuuidInfo getAccountInfo(TokenInfo token) {
		return WebClient.create("https://kr.api.riotgames.com")
			.post()
			.uri("/riot/account/v1/accounts/me")
			.header("Authorization", BEARER.getContent() + token.getAccess_token())
			.retrieve()
			.bodyToMono(PuuidInfo.class)
			.block();
	}

	@Getter
	public static class TokenInfo {

		private String access_token;
	}

	@Getter
	public static class PuuidInfo {

		private String puuid;
	}

}
