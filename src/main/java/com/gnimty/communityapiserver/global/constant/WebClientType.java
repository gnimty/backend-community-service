package com.gnimty.communityapiserver.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebClientType {

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
	GOOGLE_USER_INFO_REQUEST_URI("https://www.googleapis.com/userinfo/v2/me?access_token="),

	// GNIMTY
	GNIMTY_VERSION_URI("https://gnimty.kro.kr/asset/version"),
	GNIMTY_TOGETHER_URI("https://gnimty.kro.kr/statistics/summoners/together/%s?queue_type=%s"),
	GNIMTY_POST_SUMMONER_URI("https://gnimty.kro.kr/statistics/summoners/%s"),
	GNIMTY_GET_SUMMONER_URI("https://gnimty.kro.kr/statistics/summoners/%s-%s"),
	GNIMTY_GET_CHAMPION_INFO("https://gnimty.kro.kr/asset/champion"),

	// COMMON
	GRANT_TYPE("grant_type"),
	AUTHORIZATION_CODE("authorization_code"),
	CLIENT_ID("client_id"),
	REDIRECT_URI("redirect_uri"),
	CODE("code"),
	CLIENT_SECRET("client_secret"),
	;

	private final String value;

	public String getValue(Object... args) {
		return String.format(value, args);
	}
}
