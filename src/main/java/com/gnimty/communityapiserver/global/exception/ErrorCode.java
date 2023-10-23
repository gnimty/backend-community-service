package com.gnimty.communityapiserver.global.exception;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum ErrorCode {

	// client
	CONSTRAINT_VIOLATION(BAD_REQUEST, INVALID_INPUT_VALUE),
	URL_NOT_FOUND(NOT_FOUND, "%s : 해당 경로는 존재하지 않는 경로입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "%s : 해당 HTTP 메소드는 지원되지 않습니다. 허용 메소드 : %s"),
	MEDIA_TYPE_NOT_SUPPORTED(UNSUPPORTED_MEDIA_TYPE, "%s : 지원하지 않는 media type 입니다. 지원 type : %s"),
	ALREADY_REGISTERED_EMAIL(CONFLICT, ErrorMessage.ALREADY_REGISTERED_EMAIL),
	MEMBER_NOT_FOUND(NOT_FOUND, ErrorMessage.MEMBER_NOT_FOUND),
	TOKEN_EXPIRED(UNAUTHORIZED, ErrorMessage.TOKEN_EXPIRED),
	TOKEN_INVALID(UNAUTHORIZED, ErrorMessage.TOKEN_INVALID),
	INVALID_LOGIN(BAD_REQUEST, ErrorMessage.INVALID_LOGIN),
	TOKEN_NOT_FOUND(UNAUTHORIZED, ErrorMessage.TOKEN_NOT_FOUND),
	NO_PERMISSION(FORBIDDEN, ErrorMessage.NO_PERMISSION),
	ALREADY_LINKED_SUMMONER(CONFLICT, ErrorMessage.ALREADY_LINKED_SUMMONER),
	INVALID_EMAIL_AUTH_CODE(BAD_REQUEST, ErrorMessage.INVALID_EMAIL_AUTH_CODE),
	UNAUTHORIZED_EMAIL(UNAUTHORIZED, ErrorMessage.UNAUTHORIZED_EMAIL),
	ALREADY_LINKED_OAUTH(CONFLICT, ErrorMessage.ALREADY_LINKED_OAUTH),
	NOT_LINKED_RSO(BAD_REQUEST, ErrorMessage.NOT_LINKED_RSO),
	RIOT_ACCOUNT_NOT_FOUND(NOT_FOUND, ErrorMessage.RIOT_ACCOUNT_NOT_FOUND),
	INTRODUCTION_NOT_FOUND(NOT_FOUND, ErrorMessage.INTRODUCTION_NOT_FOUND),
	MAIN_CONTENT_MUST_BE_ONLY(CONFLICT, ErrorMessage.MAIN_CONTENT_MUST_BE_ONLY),
	EXCEED_INTRODUCTION_COUNT(BAD_REQUEST, ErrorMessage.EXCEED_INTRODUCTION_COUNT),
	DUPLICATED_ID(CONFLICT, ErrorMessage.DUPLICATED_ID),
	NOT_LOGIN_BY_FORM(BAD_REQUEST, ErrorMessage.NOT_LOGIN_BY_FORM),
	ALREADY_LINKED_PROVIDER(CONFLICT, ErrorMessage.ALREADY_LINKED_PROVIDER),
	ALREADY_BLOCKED_MEMBER(CONFLICT, ErrorMessage.ALREADY_BLOCKED_MEMBER),
	BLOCK_NOT_FOUND(NOT_FOUND, ErrorMessage.BLOCK_NOT_FOUND),
	INVALID_AUTH_CODE(BAD_REQUEST, ErrorMessage.INVALID_AUTH_CODE),
	HEADER_NOT_FOUND(BAD_REQUEST, ErrorMessage.HEADER_NOT_FOUND),
	REQUEST_BODY_INVALID(BAD_REQUEST, ErrorMessage.REQUEST_BODY_INVALID),
	INVALID_PATH_VARIABLE(BAD_REQUEST, ErrorMessage.INVALID_PATH_VARIABLE),
	MEMBER_LIKE_NOT_FOUND(NOT_FOUND, ErrorMessage.MEMBER_LIKE_NOT_FOUND),
	ALREADY_MEMBER_LIKE(CONFLICT, ErrorMessage.ALREADY_MEMBER_LIKE),
	NOT_ALLOWED_SELF_LIKE(CONFLICT, ErrorMessage.NOT_ALLOWED_SELF_LIKE),
	NOT_ALLOWED_SELF_BLOCK(CONFLICT, ErrorMessage.NOT_ALLOWED_SELF_BLOCK),
	OAUTH_INFO_NOT_FOUND(NOT_FOUND, ErrorMessage.OAUTH_INFO_NOT_FOUND),
	INVALID_ENUM_VALUE(BAD_REQUEST, ErrorMessage.INVALID_ENUM_VALUE),
	MISSING_REQUEST_PARAMETER(NOT_FOUND, ErrorMessage.MISSING_REQUEST_PARAMETER),
	CHAMPION_COMMENTS_NOT_FOUND(NOT_FOUND, ErrorMessage.CHAMPION_COMMENTS_NOT_FOUND),
	SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, ErrorMessage.SERVICE_UNAVAILABLE),
	PARENT_COMMENTS_DEPTH_MUST_BE_ONE(BAD_REQUEST, ErrorMessage.PARENT_COMMENTS_DEPTH_MUST_BE_ONE),
	CHATROOM_ALREADY_EXISTS(BAD_REQUEST, ErrorMessage.CHATROOM_ALREADY_EXISTS),
	NOT_FOUND_CHAT_USER(BAD_REQUEST, ErrorMessage.NOT_FOUND_CHAT_USER),
	COMMENTS_ID_AND_CHAMPION_ID_INVALID(BAD_REQUEST,
		ErrorMessage.COMMENTS_ID_AND_CHAMPION_ID_INVALID),
	ALREADY_CHAMPION_COMMENTS_LIKE(CONFLICT, ErrorMessage.ALREADY_CHAMPION_COMMENTS_LIKE),
	CHAMPION_COMMENTS_LIKE_NOT_FOUND(NOT_FOUND, ErrorMessage.CHAMPION_COMMENTS_LIKE_NOT_FOUND),

	// server
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.status = httpStatus;
		this.message = message;
	}

	public static class ErrorMessage {

		private ErrorMessage() {
		}

		public static final String INVALID_INPUT_VALUE = "입력값 중 검증에 실패한 값이 있습니다.";
		public static final String AGREE_TERMS_MUST_BE_TRUE = "약관 동의 항목은 반드시 true여야 합니다.";
		public static final String MEMBER_NOT_FOUND = "요청에 해당하는 회원을 찾을 수 없습니다.";
		public static final String TOKEN_EXPIRED = "만료된 token 입니다.";
		public static final String TOKEN_INVALID = "유효하지 않은 token 입니다.";
		public static final String INVALID_LOGIN = "올바르지 않은 로그인 정보입니다.";
		public static final String TOKEN_NOT_FOUND = "토큰이 존재하지 않습니다.";
		public static final String NO_PERMISSION = "해당 자원에 대한 접근 권한이 없습니다.";
		public static final String ALREADY_REGISTERED_EMAIL = "이미 가입된 이메일 입니다.";
		public static final String ALREADY_LINKED_SUMMONER = "이미 연결된 소환사 입니다.";
		public static final String INVALID_EMAIL_AUTH_CODE = "만료됐거나, 올바르지 않은 이메일 인증 코드입니다.";
		public static final String UNAUTHORIZED_EMAIL = "만료됐거나, 인증되지 않은 이메일입니다. 이메일 인증이 필요합니다.";
		public static final String ALREADY_LINKED_OAUTH = "이미 연결된 oauth 정보입니다.";
		public static final String NOT_LINKED_RSO = "소환사 계정에 연동되지 않은 회원입니다.";
		public static final String RIOT_ACCOUNT_NOT_FOUND = "요청에 해당하는 소환사 계정이 존재하지 않습니다.";
		public static final String INTRODUCTION_NOT_FOUND = "요청에 해당하는 소개글이 존재하지 않습니다.";
		public static final String MAIN_CONTENT_MUST_BE_ONLY = "main에 해당하는 요소는 반드시 1개만 존재해야 합니다.";
		public static final String EXCEED_INTRODUCTION_COUNT = "소개글의 최대 개수는 3개입니다.";
		public static final String DUPLICATED_ID = "중복된 id가 존재합니다";
		public static final String NOT_LOGIN_BY_FORM = "form 로그인 유저가 아닙니다.";
		public static final String ALREADY_LINKED_PROVIDER = "이미 요청의 provider에 해당하는 oauth에 연결돼 있습니다.";
		public static final String ALREADY_BLOCKED_MEMBER = "이미 차단된 회원입니다.";
		public static final String BLOCK_NOT_FOUND = "요청에 해당하는 차단 정보가 존재하지 않습니다.";
		public static final String INVALID_AUTH_CODE = "요청한 code가 유효하지 않습니다.";
		public static final String HEADER_NOT_FOUND = "요청에 필요한 header가 존재하지 않습니다.";
		public static final String REQUEST_BODY_INVALID = "request의 type 등이 유효하지 않습니다.";
		public static final String INVALID_PATH_VARIABLE = "path variable이 유효하지 않습니다.";
		public static final String MEMBER_LIKE_NOT_FOUND = "해당 회원에 대한 좋아요가 존재하지 않습니다.";
		public static final String ALREADY_MEMBER_LIKE = "이미 좋아요 한 회원입니다.";
		public static final String NOT_ALLOWED_SELF_LIKE = "자기 자신을 좋아요 할 수 없습니다.";
		public static final String NOT_ALLOWED_SELF_BLOCK = "자기 자신을 차단할 수 없습니다.";
		public static final String OAUTH_INFO_NOT_FOUND = "요청에 해당하는 oauth 정보가 존재하지 않습니다.";
		public static final String INVALID_ENUM_VALUE = "올바르지 않은 열거형 요청입니다.";
		public static final String MISSING_REQUEST_PARAMETER = "request parameter 중 %s는 필수입니다.";
		public static final String CHAMPION_COMMENTS_NOT_FOUND = "요청에 해당하는 챔피언 운용법이 존재하지 않습니다.";
		public static final String SERVICE_UNAVAILABLE = "외부 서비스에 요청에 대한 응답을 받을 수 없습니다.";
		public static final String PARENT_COMMENTS_DEPTH_MUST_BE_ONE = "부모 댓글은 depth를 가질 수 없습니다.";
		public static final String COMMENTS_ID_AND_CHAMPION_ID_INVALID = "commentsId와 championId가 일치하지 않습니다.";
		public static final String CHATROOM_ALREADY_EXISTS = "해당 채팅방이 이미 존재합니다.";
		public static final String NOT_FOUND_CHAT_USER = "유저 정보를 찾을 수 없습니다. 존재하지 않는 userId이거나 Riot Account에 연동되지 않은 유저입니다.";
		public static final String ALREADY_CHAMPION_COMMENTS_LIKE = "이미 좋아요 또는 싫어요를 한 댓글입니다.";
		public static final String CHAMPION_COMMENTS_LIKE_NOT_FOUND = "좋아요 또는 싫어요를 하지 않은 댓글입니다.";
	}
}