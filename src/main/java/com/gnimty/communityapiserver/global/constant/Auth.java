package com.gnimty.communityapiserver.global.constant;

import lombok.Getter;

@Getter
public enum Auth {

    // 10분
    ACCESS_TOKEN_EXPIRATION(1_000L * 60 * 10),
    // 2주
    REFRESH_TOKEN_EXPIRATION(1_000L * 60 * 60 * 24 * 14),
    // 3분
    EMAIL_CODE_EXPIRATION(1_000L * 60 * 3),
    // 10분
    SIGNUP_EXPIRATION(1_000L * 60 * 10),
    PASSWORD_EXPIRATION(1_000L * 60 * 10),

    SUBJECT_ACCESS_TOKEN("accessToken"),
    SUBJECT_REFRESH_TOKEN("refreshToken"),
    BEARER("Bearer "),
    AUTHORIZATION("Authorization"),
    EMAIL_SUBJECT("gnimty : 이메일 인증번호 안내");

    private long expiration;
    private String content;

    Auth(long expiration) {
        this.expiration = expiration;
    }

    Auth(String content) {
        this.content = content;
    }
}
