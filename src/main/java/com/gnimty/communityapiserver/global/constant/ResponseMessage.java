package com.gnimty.communityapiserver.global.constant;

import lombok.Getter;

@Getter
public enum ResponseMessage {
    SUCCESS_SUMMONER_LINK("성공적으로 소환사 계정 연결을 완료하였습니다."),
    SUCCESS_KAKAO_LINK("성공적으로 카카오 계정 연결을 완료하였습니다."),
    SUCCESS_GOOGLE_LINK("성공적으로 구글 계정 연결을 완료하였습니다."),
    SUCCESS_SIGN_UP("성공적으로 회원가입을 완료하였습니다."),
    SUCCESS_SEND_EMAIL_AUTH_CODE("성공적으로 이메일 인증 코드를 발송하였습니다."),
    SUCCESS_VERIFY_EMAIL("이메일 인증을 성공적으로 완료하였습니다."),
    SUCCESS_UPDATE_PROFILE("성공적으로 프로필 수정을 완료하였습니다."),
    SUCCESS_UPDATE_PASSWORD("성공적으로 비밀번호 변경을 완료하였습니다."),
    SUCCESS_UPDATE_SUMMONERS("성공적으로 소환사 정보를 변경하였습니다."),
    SUCCESS_UPDATE_SCHEDULES("성공적으로 게임 가능 시간를 변경하였습니다."),
    SUCCESS_UPDATE_STATUS("성공적으로 접속 상태를 변경했습니다."),
    SUCCESS_UPDATE_INTRODUCTION("성공적으로 상태 메세지를 변경했습니다."),
    SUCCESS_UPDATE_PREFER_GAME_MODE("성공적으로 선호 게임 타입을 변경했습니다."),
    SUCCESS_BLOCK("성공적으로 회원 차단을 완료하였습니다."),
    SUCCESS_CLEAR_BLOCK("성공적으로 차단 해제를 완료하였습니다."),
    SUCCESS_MEMBER_LIKE("회원 좋아요에 대한 수정을 완료하였습니다."),
    SUCCESS_DISCONNECT_OAUTH("Oauth 연결 해제를 완료하였습니다."),
    SUCCESS_LOGOUT("성공적으로 로그아웃 하였습니다."),
    SUCCESS_WITHDRAWAL("성공적으로 회원탈퇴를 완료하였습니다."),
    SUCCESS_ADD_CHAMPION_COMMENTS("성공적으로 챔피언 운용법을 작성했습니다."),
    SUCCESS_UPDATE_CHAMPION_COMMENTS("성공적으로 챔피언 운용법을 수정했습니다."),
    SUCCESS_DELETE_CHAMPION_COMMENTS("성공적으로 챔피언 운용법을 삭제했습니다."),
    SUCCESS_CHAMPION_COMMENTS_LIKE("성공적으로 챔피언 운용법 좋아요/싫어요에 대한 수정을 완료하였습니다."),
    SUCCESS_COMMENTS_REPORT("성공적으로 챔피언 운용법에 대한 신고를 완료하였습니다.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
