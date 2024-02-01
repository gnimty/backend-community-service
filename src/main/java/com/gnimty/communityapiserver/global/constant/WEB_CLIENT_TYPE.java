package com.gnimty.communityapiserver.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WEB_CLIENT_TYPE {

	// KAKAO
	KAKAO_ACCOUNT_REQUEST_URI("https://kapi.kakao.com/v2/user/me"),
	KAKAO_TOKEN_REQUEST_URI("https://kauth.kakao.com/oauth/token"),
	PROPERTY_KEYS("property_keys"),
	EMAIL_PROPERTY_KEY("[\"kakao_account.email\"]"),

	// RIOT
	RIOT_TOKEN_REQUEST_URI("https://auth.riotgames.com/token"),
	RIOT_ACCOUNT_REQUEST_URI("https://asia.api.riotgames.com/riot/account/v1/accounts/me"),

	// GOOGLE
	GOOGLE_TOKEN_REQUEST_URI("https://oauth2.googleapis.com/token"),

	// COMMON
	HEADER_AUTHORIZATION("Authorization"),
	GRANT_TYPE("grant_type"),
	AUTHORIZATION_CODE("authorization_code"),
	CLIENT_ID("client_id"),
	REDIRECT_URI("redirect_uri"),
	CODE("code"),
	CLIENT_SECRET("client_secret"),
	;

	private final String value;
}
