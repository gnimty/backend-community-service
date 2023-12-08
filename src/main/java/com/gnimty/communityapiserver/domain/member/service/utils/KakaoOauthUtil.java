package com.gnimty.communityapiserver.domain.member.service.utils;

import static com.gnimty.communityapiserver.global.constant.Auth.BEARER;

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
public class KakaoOauthUtil {

	@Value("${oauth.kakao.client_id}")
	private String client_id;
	@Value("${oauth.kakao.client_secret}")
	private String client_secret;
	@Value("${oauth.kakao.redirect_uri}")
	private String redirect_uri;


	public String getKakaoUserEmail(String authCode) {
		AccountInfo accountInfo;
		try {
			TokenInfo block = getTokenInfo(authCode);
			accountInfo = getAccountInfo(block);
		} catch (WebClientResponseException e) {
			throw new BaseException(ErrorCode.INVALID_AUTH_CODE);
		}
		return accountInfo.getKakao_account().getEmail();
	}

	private AccountInfo getAccountInfo(TokenInfo block) {
		return WebClient.create("https://kapi.kakao.com")
			.post()
			.uri("/v2/user/me")
			.body(BodyInserters.fromFormData("property_keys", "[\"kakao_account.email\"]"))
			.header("Authorization", BEARER.getContent() + block.getAccess_token())
			.retrieve()
			.bodyToMono(AccountInfo.class)
			.block();
	}

	private TokenInfo getTokenInfo(String authCode) {
		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add("grant_type", "authorization_code");
		bodyMap.add("client_id", client_id);
		bodyMap.add("redirect_uri", redirect_uri);
		bodyMap.add("code", authCode);
		bodyMap.add("client_secret", client_secret);

		return WebClient.create("https://kauth.kakao.com")
			.post()
			.uri("/oauth/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(BodyInserters.fromFormData(bodyMap))
			.retrieve()
			.bodyToMono(TokenInfo.class)
			.block();
	}

	@Getter
	public static class TokenInfo {

		private String access_token;
	}

	@Getter
	public static class AccountInfo {

		private KakaoAccount kakao_account;
	}

	@Getter
	public static class KakaoAccount {

		private String email;
	}
}
