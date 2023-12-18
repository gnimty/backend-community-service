package com.gnimty.communityapiserver.global.constant;

import lombok.Getter;

@Getter
public enum CommonStringType {

	EMPTY(""),
	SIGNUP_EMAIL_TEMPLATE("signup-mail"),
	SIGNUP_EMAIL_BANNER("static/images/banner-pengu.png"),
	PASSWORD_EMAIL_TEMPLATE("password-mail"),
	PASSWORD_EMAIL_BANNER("static/images/banner-urf.png"),
	VERIFY_SIGNUP("verified"),
	TAG_SPLITTER("#"),
	URL_TAG_SPLITTER("-"),
	;

	private final String value;

	CommonStringType(String value) {
		this.value = value;
	}
}
