package com.gnimty.communityapiserver.global.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -14522992572249166L;

	private final ErrorCode errorCode;
	private final String message;

	public BaseException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
	}

	public BaseException(ErrorCode errorCode, String message) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.message = message;
	}
}
