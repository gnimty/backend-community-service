package com.gnimty.communityapiserver.global.constant;

public class RequestPattern {

    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-z A-Z]{2,7}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_{|}~]).{8,16}$";
    public static final String EMAIL_AUTH_CODE_PATTERN = "^[A-Z0-9]{6}$";
}
