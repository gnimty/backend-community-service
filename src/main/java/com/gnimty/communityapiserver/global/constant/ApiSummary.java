package com.gnimty.communityapiserver.global.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiSummary {

	public static final String READ_BLOCKS = "차단 목록 조회";
	public static final String DO_BLOCK = "회원 차단";
	public static final String CLEAR_BLOCK = "회원 차단 해제";
	public static final String READ_CHAMPION_COMMENTS = "챔피언 운용법 조회";
	public static final String ADD_CHAMPION_COMMENTS = "챔피언 운용법 추가";
	public static final String UPDATE_CHAMPION_COMMENTS = "챔피언 운용법 수정";
	public static final String DELETE_CHAMPION_COMMENTS = "챔피언 운용법 삭제";
	public static final String DO_CHAMPION_COMMENTS_LIKE = "챔피언 운용법 좋아요/싫어요 또는 취소";
	public static final String DO_REPORT = "챔피언 운용법 신고";
	public static final String SIGNUP = "form 회원가입";
	public static final String LOGIN = "form 로그인";
	public static final String KAKAO_LOGIN = "kakao 회원 가입(최초 로그인)";
	public static final String GOOGLE_LOGIN = "google 회원 가입(최초 로그인)";
	public static final String SEND_EMAIL_AUTH_CODE = "회원가입 email 인증 코드 전송";
	public static final String VERIFY_EMAIL_AUTH_CODE = "회원가입 email 인증 코드 확인(검증)";
	public static final String TOKEN_REFRESH = "토큰 재발급";
	public static final String SUMMONER_ACCOUNT_LINK = "riot 로그인 및 계정 연동";
	public static final String KAKAO_ADDITIONAL_LINK = "kakao 추가 로그인";
	public static final String GOOGLE_ADDITIONAL_LINK = "google 추가 로그인";
	public static final String GET_MY_PROFILE = "내 프로필 조회";
	public static final String UPDATE_MY_PROFILE_MAIN = "메인 화면 내 프로필 수정";
	public static final String SEND_PASSWORD_EMAIL_AUTH_CODE = "비밀번호 재설정 인증 코드 발송";
	public static final String VERIFY_PASSWORD_EMAIL_AUTH_CODE = "비밀번호 재설정 인증 코드 확인(검증)";
	public static final String RESET_PASSWORD = "비밀번호 재설정";
	public static final String UPDATE_PASSWORD = "비밀번호 변경";
	public static final String UPDATE_MY_PROFILE = "내 프로필 수정(내 정보 화면)";
	public static final String DELETE_OAUTH_INFO = "Oauth 인증 정보 제거";
	public static final String LOGOUT = "로그아웃";
	public static final String WITHDRAWAL = "회원탈퇴";
	public static final String GET_OTHER_PROFILE = "다른 회원 프로필 확인";
	public static final String READ_MEMBER_LIKE = "나의 좋아요 개수 조회";
	public static final String DO_MEMBER_LIKE = "회원 좋아요 또는 취소";
	public static final String UPDATE_SUMMONERS = "소환사 정보 bulk update";
	public static final String GET_RECOMMENDED_SUMMONERS = "듀오 추천 소환사 조회";
	public static final String GET_MAIN_SUMMONERS = "메인 화면 추천 소환사 조회";
	public static final String GET_RECENTLY_SUMMONERS = "최근 같이 게임한 소환사 상위 9명 조회";
}
