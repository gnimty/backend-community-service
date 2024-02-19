package com.gnimty.communityapiserver.domain.member.service.utils;

import static com.gnimty.communityapiserver.global.constant.Auth.BEARER;
import static com.gnimty.communityapiserver.global.constant.WebClientType.AUTHORIZATION_CODE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.CLIENT_ID;
import static com.gnimty.communityapiserver.global.constant.WebClientType.CLIENT_SECRET;
import static com.gnimty.communityapiserver.global.constant.WebClientType.CODE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.EMAIL_PROPERTY_KEY;
import static com.gnimty.communityapiserver.global.constant.WebClientType.GRANT_TYPE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.KAKAO_ACCOUNT_REQUEST_URI;
import static com.gnimty.communityapiserver.global.constant.WebClientType.KAKAO_TOKEN_REQUEST_URI;
import static com.gnimty.communityapiserver.global.constant.WebClientType.PROPERTY_KEYS;
import static com.gnimty.communityapiserver.global.constant.WebClientType.REDIRECT_URI;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import com.gnimty.communityapiserver.global.dto.webclient.KakaoAccountInfo;
import com.gnimty.communityapiserver.global.dto.webclient.KakaoTokenInfo;
import com.gnimty.communityapiserver.global.utils.WebClientUtil;
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

	public String getKakaoUserEmail(String authCode, String redirectUri) {
		KakaoTokenInfo kakaoTokenInfo = WebClientUtil.post(KakaoTokenInfo.class, KAKAO_TOKEN_REQUEST_URI.getValue(),
			APPLICATION_FORM_URLENCODED, getTokenInfoBodyMap(authCode, redirectUri), null);
		KakaoAccountInfo kakaoAccountInfo = WebClientUtil.post(KakaoAccountInfo.class,
			KAKAO_ACCOUNT_REQUEST_URI.getValue(), APPLICATION_FORM_URLENCODED, getAccountInfoBodyMap(),
			httpHeaders -> httpHeaders.set(AUTHORIZATION, BEARER.getContent() + kakaoTokenInfo.getAccess_token()));
		return kakaoAccountInfo.getKakao_account().getEmail();
	}

	private MultiValueMap<String, String> getTokenInfoBodyMap(String authCode, String redirectUri) {
		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add(GRANT_TYPE.getValue(), AUTHORIZATION_CODE.getValue());
		bodyMap.add(CLIENT_ID.getValue(), client_id);
		bodyMap.add(REDIRECT_URI.getValue(), redirectUri);
		bodyMap.add(CODE.getValue(), authCode);
		bodyMap.add(CLIENT_SECRET.getValue(), client_secret);
		return bodyMap;
	}

	private MultiValueMap<String, String> getAccountInfoBodyMap() {
		LinkedMultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add(PROPERTY_KEYS.getValue(), EMAIL_PROPERTY_KEY.getValue());
		return bodyMap;
	}
}
