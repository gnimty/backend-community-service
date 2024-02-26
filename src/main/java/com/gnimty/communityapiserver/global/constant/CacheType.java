package com.gnimty.communityapiserver.global.constant;

import static com.gnimty.communityapiserver.global.constant.Auth.EMAIL_CODE_EXPIRATION;
import static com.gnimty.communityapiserver.global.constant.Auth.PASSWORD_EXPIRATION;
import static com.gnimty.communityapiserver.global.constant.Auth.REFRESH_TOKEN_EXPIRATION;
import static com.gnimty.communityapiserver.global.constant.Auth.SIGNUP_EXPIRATION;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CacheType {

	REFRESH_TOKEN("refreshTokenCache", REFRESH_TOKEN_EXPIRATION.getExpiration(), 10000),
	SIGNUP_VERIFIED("signupVerifiedCache", SIGNUP_EXPIRATION.getExpiration(), 1000),
	SIGNUP_EMAIL_CODE("signupEmailCodeCache", EMAIL_CODE_EXPIRATION.getExpiration(), 1000),
	RESET_PASSWORD_EMAIL_CODE("resetPasswordEmailCodeCache", EMAIL_CODE_EXPIRATION.getExpiration(), 1000),
	UPDATE_PASSWORD_CODE("updatePasswordCodeCache", PASSWORD_EXPIRATION.getExpiration(), 1000),
	;

	private final String cacheName;
	private final long expiredTime;
	private final long maximumSize;
}
