package com.gnimty.communityapiserver.global.constant;

public class ApiDescription {

	public static final String READ_BLOCKS = """
		회원의 차단 목록을 조회하는 API 입니다.
		""";
	public static final String DO_BLOCK = """
		차단할 회원을 추가하는 API 입니다.
		   
		차단하는 회원의 기준은 연결된 소환사가 아닌, “회원” 입니다.
		""";
	public static final String CLEAR_BLOCK = """
		차단한 회원을 차단 해제하는 API 입니다.
		   
		회원 차단 조회 시 응답으로 가는 “차단 정보 id”를 통해 차단을 해제할 수 있습니다.
		   
		만약 차단 정보 id가 올바르지 않을 경우, No Permission 예외를 응답합니다.
		""";
	public static final String READ_CHAMPION_COMMENTS = """
		champion_id에 해당하는 챔피언 운용법을 조회하는 API 입니다.
		   
		회원이 미인증 상태인 경우 blocked는 항상 false입니다.
		""";
	public static final String ADD_CHAMPION_COMMENTS = """
		champion_id에 해당하는 챔피언 운용법을 작성하는 API 입니다.
		   
		parentcommentID가 null이 아닐 경우 depth는 반드시 1이어야 합니다.
					
		올바른 championId, opponentChampionId, mentionedMemberId를 작성해야 합니다.
		   
		depth가 0일 경우(부모 댓글일 경우),
					
		lane, opponentChampionId, commentsType을 작성할 수 있습니다.
		   
		depth가 1일 경우(자식 댓글일 경우),
					
		lane, opponentChampionId, commentsType을 작성할 수 없습니다.
					
		부모 댓글과 같은 버전의 상태에서만 댓글을 작성할 수 있습니다.
		""";
	public static final String UPDATE_CHAMPION_COMMENTS = """
		comments_id에 해당하는 챔피언 운용법을 수정하는 API 입니다.
					
		챔피언 운용법 수정하는 경우, mentionedMemberId와 contents만 수정 가능합니다.
		   
		comments_id는 반드시 내가 작성한 챔피언 운용법이어야 합니다.
		""";
	public static final String DELETE_CHAMPION_COMMENTS = """
		comments_id에 해당하는 챔피언 운용법을 삭제하는 API 입니다.
		   
		comments_id는 반드시 내가 작성한 챔피언 운용법 이어야 합니다.
					
		삭제 후 해당 챔피언 운용법은 soft delete 처리 됩니다.
		""";
	public static final String DO_CHAMPION_COMMENTS_LIKE = """
		comments_id에 해당하는 챔피언 운용법에 반응 또는 반응 취소를 하는 기능입니다.
		   
		하나의 comments 당 좋아요/싫어요는 최대 1번 가능합니다.
		   
		likeOrNot이 true인 경우, cancel이 true인 경우 좋아요 취소, false인 경우 좋아요 를 수행합니다.
					
		likeOrNot이 false인 경우, cancel이 true인 경우 싫어요 취소, false인 경우 싫어요를 수행합니다.
		   
		좋아요 또는 싫어요를 수행하지 않은 챔피언 운용법에 cancel = true로 요청하였거나,
					
		좋아요 또는 싫어요를 수행한 챔피언 운용법에 cancel = false로 요청하면 실패합니다.
		""";
	public static final String DO_REPORT = """
		comments_id에 해당하는 챔피언 운용법에 신고를 하는 기능입니다.
		   
		하나의 comments 당 신고는 최대 1번 가능합니다.
					
		RSO연동 이후에만 신고가 가능합니다.
					
		reportType에 OTHER이 존재할 경우, 반드시 reportComment가 존재해야 합니다.
		""";
	public static final String SIGNUP = """
		이메일, 비밀번호가 정규 표현식에 위배되지 않고, 약관 동의가 true여야 회원가입이 가능합니다.
					
		이메일 인증 이후에 회원가입이 완료 되어야 합니다.
					
		이메일 인증 직후, 회원 가입 만료까지 약 10분의 이메일 인증 유효 시간이 주어집니다.
					
		이메일은 중복될 수 없습니다.
					
		최초 form 회원가입 이후 익명의소환사#pk 형식으로 자동으로 닉네임이 부여됩니다.
		""";
	public static final String LOGIN = """
		로그인 시 response body로 access token, refresh token이 응답됩니다. 또한 기존 존재하던 refresh token의 정보는 삭제 됩니다.
					
		이메일 또는 비밀번호를 잘못 입력했을 경우, 어떤 필드의 입력이 올바르지 않던 간에 “이메일 또는 비밀번호가 올바르지 않습니다” 식의 응답이 반환됩니다.
					
		또한, 로그인 시에는 아이디와 비밀번호의 정규 표현식을 확인하지 않습니다.
		""";
	public static final String KAKAO_LOGIN = """
		kakao social login을 진행하는 API 입니다.
					
		카카오 로그인 후 발급되는 인가 코드를 request body에 담아 요청하면 됩니다.
		""";
	public static final String GOOGLE_LOGIN = """
		google social login을 진행하는 API 입니다.
					
		구글 로그인 후 발급되는 인가 코드를 request body에 담아 요청하면 됩니다.
		""";
	public static final String SEND_EMAIL_AUTH_CODE = """
		이메일을 입력받아 해당 이메일로 인증 번호를 발송하는 API입니다.
					
		인증번호의 유효 시간은 3분 입니다.
					
		비동기 처리 방식으로, 서버 상태에 따라 발송 시간이 달라질 수 있습니다.
		""";
	public static final String VERIFY_EMAIL_AUTH_CODE = """
		이메일을 인증 코드를 입력받아 검증하는 API입니다.
					
		인증 번호를 올바르게 입력하면, 해당 email에 대한 회원가입 유효 기간이 10분 주어집니다.
					
		즉, 이메일 인증 코드 확인 이후 10분 내에 회원가입을 완료하여야 합니다.
		""";
	public static final String TOKEN_REFRESH = """
		access token 만료 시 refresh token을 통해 재발급받는 API입니다.
					
		refresh token 조차 만료됐을 경우, 예외를 반환하며 다시 로그인을 진행해야 합니다.
					
		기존 refresh token의 재사용 방지를 위해 accessToken과 함께 refreshToken도 재발급됩니다.
		""";
	public static final String SUMMONER_ACCOUNT_LINK = """
		Riot 소환사 계정 연결하는 API입니다.
					
		최초 라이엇 로그인 성공 시, 소환사 이름을 포함한 정보만 update 됩니다.
					
		이후 전적 서버(statistics)와 통신하며 전적 정보들을 갱신하게 되는데, 이는 비동기로 처리됩니다.
		""";
	public static final String KAKAO_ADDITIONAL_LINK = """
		해당 API는 최초 폼 회원가입 회원에게만 사용 가능합니다.
		""";
	public static final String GOOGLE_ADDITIONAL_LINK = """
		해당 API는 최초 폼 회원가입 회원에게만 사용 가능합니다.
		""";
	public static final String GET_MY_PROFILE = """
		내 정보 조회하는 API입니다.

		cookie로 access token을 담아 요청하면 내 정보가 조회됩니다.

		RSO 연동한 계정만 소개글, 선호 플레이 타임, 선호 큐 타입이 조회됩니다.

		RSO 연동하지 않은 계정은 null이 반환됩니다.

		닉네임의 경우
				
		- RSO 이전: 처음 회원 가입 시 임의의 닉네임이 발급되며 이후 변경할 수 있습니다.
				
		- RSO 이후: 대표 소환사 계정의 닉네임입니다.
		""";
	public static final String UPDATE_MY_PROFILE_MAIN = """
		회원의 프로필(대표 소환사 계정, 상태, 상태 메시지)을 변경하는 API 입니다.
					
		riot 연동 이후에만 요청 가능합니다.
		   
		소환사 계정에 연결되지 않았다면 예외를 반환합니다.
					
		mainRiotAccountId가 자신의 라이엇 계정 id가 아니면 예외를 반환합니다.
		   
		@IntroductionEntry 의 content, isMain은 NotNull이며, content의 최대 길이는 90입니다.
					
		introductions의 id가 null이라면 소개글 추가를, id가 null이 아니라면 해당하는 소개글의 수정을 의미합니다.
					
		introductions의 id가 자신의 소개글 id가 아니면 예외를 반환합니다.
					
		introductions의 id가 중복되면 예외를 반환합니다.
					
		introductions의 isMain이 중복되면 예외를 반환합니다.
					
		현재 존재하는 소개글의 개수 + 추가하는 소개글의 개수가 3개 초과라면 예외를 반환합니다.
					
		소개글 추가, 수정 이후 대표 소개글이 2개 이상 존재한다면 예외를 반환합니다.
		""";
	public static final String SEND_PASSWORD_EMAIL_AUTH_CODE = """
		회원의 email로 인증 코드를 전송하는 API 입니다.
					
		회원의 Form 로그인 정보가 없으면 예외를 반환합니다.
		   
		인증번호의 유효 시간은 3분 입니다.
		   
		비동기 처리 방식으로, 서버 상태에 따라 발송 시간이 달라질 수 있습니다.
		""";
	public static final String VERIFY_PASSWORD_EMAIL_AUTH_CODE = """
		인증 코드를 입력받아 검증하는 API입니다.
					
		인증 번호를 올바르게 입력하면, 해당 회원에 대한 UUID(exp: 10m)가 주어집니다.
		   
		이후 비밀번호 재설정 시, 해당 UUID를 함께 요청해야 합니다.
		""";
	public static final String RESET_PASSWORD = """
		회원의 비밀번호를 재설정하는 API입니다.
		   
		이메일 인증을 마친 이후여야 가능합니다.
					
		인증 번호 검증 이후, 응답된 UUID를 함께 요청해야 합니다.
		""";
	public static final String UPDATE_PASSWORD = """
		회원의 비밀번호를 변경하는 API입니다.
		   
		form 로그인 회원인 경우만 가능합니다.
		""";
	public static final String UPDATE_MY_PROFILE = """
		회원의 프로필(대표 소환사 계정, 상태, 상태 메시지)을 변경하는 API 입니다.
					
		riot 연동 이후에만 요청 가능합니다.
		   
		소환사 계정에 연결되지 않았다면 예외를 반환합니다.
					
		mainRiotAccountId가 자신의 라이엇 계정 id가 아니면 예외를 반환합니다.
		   
		@IntroductionEntry 의 content, isMain은 NotNull이며, content의 최대 길이는 90입니다.
					
		introductions의 id가 null이라면 소개글 추가를, id가 null이 아니라면 해당하는 소개글의 수정을 의미합니다.
					
		introductions의 id가 자신의 소개글 id가 아니면 예외를 반환합니다.
					
		introductions의 id가 중복되면 예외를 반환합니다.
					
		introductions의 isMain이 중복되면 예외를 반환합니다.
					
		현재 존재하는 소개글의 개수 + 추가하는 소개글의 개수가 3개 초과라면 예외를 반환합니다.
					
		소개글 추가, 수정 이후 대표 소개글이 2개 이상 존재한다면 예외를 반환합니다.
		""";
	public static final String DELETE_OAUTH_INFO = """
		연결된 oauth 계정을 해제하는 API 입니다.
		   
		최초 Form 로그인 이후, 구글 계정을 추가로 연동한 이후에만 가능합니다.
		""";
	public static final String LOGOUT = """
		로그 아웃을 진행하는 API 입니다.
		   
		저장돼 있던 연결 정보(refresh token)를 삭제합니다.
		""";
	public static final String WITHDRAWAL = """
		회원 탈퇴를 진행하는 API 입니다.
		""";
	public static final String GET_OTHER_PROFILE = """
		다른 회원의 정보를 조회하는 API입니다.
					
		cookie가 필요하지 않습니다.
					
		RSO 연동한 계정만 소개글, 선호 플레이 타임, 선호 큐 타입이 조회됩니다.
					
		RSO 연동하지 않은 계정은 null이 반환됩니다.
		""";
	public static final String READ_MEMBER_LIKE = """
		나의 좋아요 개수를 조회하는 API 입니다.
		""";
	public static final String DO_MEMBER_LIKE = """
		회원에 대해 좋아요/좋아요 취소 기능을 하는 API 입니다.
		   
		targetMemberId에 대해
					
		cancel = true인 경우, 좋아요 취소 기능을 수행합니다. 기존 좋아요가 존재하지 않을 경우, 예외를 반환합니다.
					
		cancel = false인 경우, 좋아요 기능을 수행합니다. 이미 좋아요가 존재할 경우, 예외를 반환합니다.
		   
		targetMemberId = member_id인 경우, 예외를 반환합니다.
		""";
	public static final String UPDATE_SUMMONERS = """
		user endpoint로 사용할 수 없는 API입니다.
		   
		bulk update를 통해 “커뮤니티 서비스에 연결된 소환사” 목록 중 일부를 변경하는 API입니다.
		""";
	public static final String GET_RECOMMENDED_SUMMONERS = """
		pageSize에 해당하는 수의 소환사를 추천하는 API 입니다.
		   
		해당 API는 cursor base paging이 적용되어 있으므로, 아래와 같은 검증 과정을 거치게 됩니다.
		   
		- sortBy는 정렬 조건으로, null일 수 없습니다.

		- lastSummonerId와, sortBy에 해당하는 정렬 조건 필드는 모두 null이거나, 모두 null이 아니어야 합니다.
				
		- lastSummonerId와, sortBy에 해당하는 정렬 조건 필드 모두 null인 경우: 최초 페이지 요청 시(0번 페이지)
		
		- 이외의 경우: n번 페이지
				
		예를들어,

		- sortBy가 ATOZ(이름순)일 경우, (lastSummonerId == null && lastName == null) 또는, (lastSummonerId != null && lastName != null)
					
		- sortBy가 RECOMMEND(추천순)일 경우, (lastSummonerId == null && lastSummonerUpCount == null) 또는, (lastSummonerId != null && lastSummonerUpCount != null)
					
		- sortBy가 TIER(티어순)일 경우, (lastSummonerId == null && lastSummonerMmr == null) 또는, (lastSummonerId != null && lastSummonerMmr != null)
				
		이어야 합니다.
		""";
	public static final String GET_MAIN_SUMMONERS = """
		@GameMode 에 해당하는 소환사들의 목록을 조회하는 API 입니다.
		   
		다음과 같은 기준으로 조회됩니다.
		   
		0. 공통 필터
					
		@Status가 ONLINE인 소환사
					
		요청한 와 일치하는 소환사
		   
		1. rso와 연결된 경우
					
		듀오 가능한 소환사 5명을 랜덤으로 조회
		   
		2. rso와 연결되지 않은 경우
					
		소환사 5명을 랜덤으로 조회
		""";
	public static final String GET_RECENTLY_SUMMONERS = """
		다음과 같은 조건으로 소환사를 조회합니다.
		   
		- 최근 20게임에서 같이 한 소환사 목록 불러오기 (최대 80개)
					
		- **내 채팅방에 소속된 유저**(+딸려있는 연동 소환사) 목록 불러오기 결과에서 내가 up 안한 사람들 거르기
		""";

	public static final String GET_UP_COUNT = """
		해당 전적 검색 아이디가 community 서비스에 회원가입 돼 있지 않은 경우, null을 응답합니다.
		""";
}
