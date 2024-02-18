package com.gnimty.communityapiserver.domain.member.service.utils;

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
public class GoogleOauthUtil {

	@Value("${oauth.google.client_id}")
	private String client_id;
	@Value("${oauth.google.client_secret}")
	private String client_secret;
	private final JwtProvider jwtProvider;

	public String getGoogleUserEmail(String authCode, String redirectUri) {
		UserInfo userInfo;
		try {
			TokenInfo tokenInfo = getTokenInfo(authCode, redirectUri);
			userInfo = getUserInfo(tokenInfo.getAccess_token());
		} catch (WebClientResponseException e) {
			throw new BaseException(ErrorCode.INVALID_AUTH_CODE);
		}
		return userInfo.getEmail();
	}

	private TokenInfo getTokenInfo(String authCode, String redirectUri) {
		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("grant_type", "authorization_code");
		bodyMap.add("client_id", client_id);
		bodyMap.add("client_secret", client_secret);
		bodyMap.add("redirect_uri", redirectUri);
		bodyMap.add("code", authCode);
		return WebClient.create("https://oauth2.googleapis.com")
			.post()
			.uri("/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(bodyMap))
			.retrieve()
			.bodyToMono(TokenInfo.class)
			.block();
	}

	private UserInfo getUserInfo(String accessToken) {
		return WebClient.create("https://www.googleapis.com")
			.get()
			.uri("/userinfo/v2/me?access_token=" + accessToken)
			.retrieve()
			.bodyToMono(UserInfo.class)
			.block();
	}

	@Getter
	public static class TokenInfo {

		private String access_token;
	}

	@Getter
	public static class UserInfo {

		private String email;
	}
}
