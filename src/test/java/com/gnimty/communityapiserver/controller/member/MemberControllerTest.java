package com.gnimty.communityapiserver.controller.member;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_DISCONNECT_OAUTH;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_GOOGLE_LINK;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_KAKAO_LINK;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_LOGOUT;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_SEND_EMAIL_AUTH_CODE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_SUMMONER_LINK;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_INTRODUCTION;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_PASSWORD;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_PREFER_GAME_MODE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_PROFILE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_STATUS;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_WITHDRAWAL;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.MISSING_REQUEST_PARAMETER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gnimty.communityapiserver.controller.ControllerTestSupport;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.IntroductionUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.MyProfileUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.OauthLoginRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.PasswordEmailVerifyRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.PasswordResetRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.PasswordUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.PreferGameModeUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.SendEmailRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.StatusUpdateRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.IntroductionUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.OauthLoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordEmailVerifyServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordResetServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PreferGameModeUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SendEmailServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.StatusUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.MyProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OauthInfoEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OtherProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PasswordEmailVerifyServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.RiotAccountEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.RiotDependentInfo;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.global.constant.DayOfWeek;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.constant.Status;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

public class MemberControllerTest extends ControllerTestSupport {

	@BeforeEach
	void setUp() throws Exception {
		given(memberAuthInterceptor.preHandle(
			any(HttpServletRequest.class),
			any(HttpServletResponse.class),
			any(Object.class)))
			.willReturn(true);

		given(tokenAuthInterceptor.preHandle(
			any(HttpServletRequest.class),
			any(HttpServletResponse.class),
			any(Object.class)))
			.willReturn(true);
	}

	@Nested
	@DisplayName("rso 연동 시")
	class SummonerAccountLink {

		private static final String REQUEST_URL = "/members/{member_id}/rso";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청을 보내면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {
			OauthLoginRequest request = OauthLoginRequest.builder()
				.authCode("authCode")
				.build();

			given(memberService.summonerAccountLink(any(OauthLoginServiceRequest.class)))
				.willReturn(null);
			willDoNothing()
				.given(stompService)
				.createOrUpdateUser(any(RiotAccount.class));

			mockMvc.perform(post(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message")
						.value(SUCCESS_SUMMONER_LINK.getMessage())
				);
		}

		@DisplayName("authCode를 입력하지 않으면 실패한다.")
		@Test
		void should_fail_when_authCodeIsNull() throws Exception {
			OauthLoginRequest request = OauthLoginRequest.builder()
				.authCode(null)
				.build();

			given(memberService.summonerAccountLink(any(OauthLoginServiceRequest.class)))
				.willReturn(null);
			willDoNothing()
				.given(stompService)
				.createOrUpdateUser(any(RiotAccount.class));

			mockMvc.perform(post(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}
	}

	@Nested
	@DisplayName("kakao 추가 연동 시")
	class KakaoAdditionalLink {

		private static final String REQUEST_URL = "/members/{member_id}/oauth/kakao";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청을 보내면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {
			OauthLoginRequest request = OauthLoginRequest.builder()
				.authCode("authCode")
				.build();

			willDoNothing()
				.given(memberService)
				.oauthAdditionalLink(any(Provider.class), any(OauthLoginServiceRequest.class));

			mockMvc.perform(post(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message")
						.value(SUCCESS_KAKAO_LINK.getMessage())
				);
		}

		@DisplayName("authCode를 입력하지 않으면 실패한다.")
		@Test
		void should_fail_when_authCodeIsNull() throws Exception {
			OauthLoginRequest request = OauthLoginRequest.builder()
				.authCode(null)
				.build();

			willDoNothing()
				.given(memberService)
				.oauthAdditionalLink(any(Provider.class), any(OauthLoginServiceRequest.class));

			mockMvc.perform(post(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}
	}

	@Nested
	@DisplayName("google 추가 연동 시")
	class GoogleAdditionalLink {

		private static final String REQUEST_URL = "/members/{member_id}/oauth/google";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청을 보내면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {
			OauthLoginRequest request = OauthLoginRequest.builder()
				.authCode("authCode")
				.build();

			willDoNothing()
				.given(memberService)
				.oauthAdditionalLink(any(Provider.class), any(OauthLoginServiceRequest.class));

			mockMvc.perform(post(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message")
						.value(SUCCESS_GOOGLE_LINK.getMessage())
				);
		}

		@DisplayName("authCode를 입력하지 않으면 실패한다.")
		@Test
		void should_fail_when_authCodeIsNull() throws Exception {
			OauthLoginRequest request = OauthLoginRequest.builder()
				.authCode(null)
				.build();

			willDoNothing()
				.given(memberService)
				.oauthAdditionalLink(any(Provider.class), any(OauthLoginServiceRequest.class));

			mockMvc.perform(post(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}
	}

	@Nested
	@DisplayName("내 프로필 조회 시")
	class GetMyProfile {

		private static final String REQUEST_URL = "/members/me";

		@DisplayName("올바른 요청을 하면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {

			RiotDependentInfo riotDependentInfo = RiotDependentInfo.builder()
				.isLinked(true)
				.status(Status.ONLINE)
				.riotAccounts(List.of(
					RiotAccountEntry.builder()
						.summonerName("소환사이름")
						.build()))
				.build();
			OauthInfoEntry oauthInfoEntry = OauthInfoEntry.builder()
				.email("email@email.com")
				.build();
			MyProfileServiceResponse response = MyProfileServiceResponse.builder()
				.id(1L)
				.email("email")
				.nickname("nickanme")
				.favoriteChampionId(1L)
				.upCount(100L)
				.riotDependentInfo(riotDependentInfo)
				.oauthInfos(List.of(oauthInfoEntry))
				.build();

			given(memberService.getMyProfile())
				.willReturn(response);

			mockMvc.perform(get(REQUEST_URL))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.data.id").value(response.getId()),
					jsonPath("$.data.email").value(response.getEmail()),
					jsonPath("$.data.favoriteChampionId")
						.value(response.getFavoriteChampionId()),
					jsonPath("$.data.upCount").value(response.getUpCount()),
					jsonPath("$.data.riotDependentInfo.isLinked")
						.value(response.getRiotDependentInfo().getIsLinked()),
					jsonPath("$.data.riotDependentInfo.status")
						.value(response.getRiotDependentInfo().getStatus().toString()),
					jsonPath("$.data.riotDependentInfo.riotAccounts[0].summonerName")
						.value(response.getRiotDependentInfo().getRiotAccounts().get(0)
							.getSummonerName()),
					jsonPath("$.data.oauthInfos[0].email")
						.value(response.getOauthInfos().get(0).getEmail())
				);
		}
	}

	@DisplayName("내 프로필 수정 시")
	@Nested
	class UpdateMyProfile {

		private static final String REQUEST_URL = "/members/{member_id}";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청을 하면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {
			MyProfileUpdateRequest request = createRequest(true, "content");

			given(memberService.updateMyProfile(any(Long.class),
				any(MyProfileUpdateServiceRequest.class)))
				.willReturn(null);
			willDoNothing()
				.given(stompService)
				.updateConnStatus(any(User.class), any(Status.class));
			willDoNothing()
				.given(stompService)
				.createOrUpdateUser(any(RiotAccount.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message")
						.value(SUCCESS_UPDATE_PROFILE.getMessage()));
		}

		@DisplayName("content 또는 isMain이 null이면 실패한다.")
		@Test
		void should_fail_when_contentOrIsMainIsNull() throws Exception {
			MyProfileUpdateRequest request = createRequest(null, null);

			given(memberService.updateMyProfile(any(Long.class),
				any(MyProfileUpdateServiceRequest.class)))
				.willReturn(null);
			willDoNothing()
				.given(stompService)
				.updateConnStatus(any(User.class), any(Status.class));
			willDoNothing()
				.given(stompService)
				.createOrUpdateUser(any(RiotAccount.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		@DisplayName("content가 90가 초과이면 실패한다.")
		@Test
		void should_fail_when_contentLengthExceed90() throws Exception {
			MyProfileUpdateRequest request = createRequest(true, "a".repeat(91));

			given(memberService.updateMyProfile(any(Long.class),
				any(MyProfileUpdateServiceRequest.class)))
				.willReturn(null);
			willDoNothing()
				.given(stompService)
				.updateConnStatus(any(User.class), any(Status.class));
			willDoNothing()
				.given(stompService)
				.createOrUpdateUser(any(RiotAccount.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		private MyProfileUpdateRequest createRequest(Boolean isMain, String content) {
			return MyProfileUpdateRequest.builder()
				.mainRiotAccountId(1L)
				.status(Status.AWAY)
				.introductions(List.of(IntroductionEntry.builder()
					.id(1L)
					.isMain(isMain)
					.content(content)
					.build()))
				.build();
		}
	}

	@DisplayName("비밀번호 변경 이메일 전송 시")
	@Nested
	class SendEmailAuthCode {

		private static final String REQUEST_URL = "/members/password/email";

		@DisplayName("form 로그인 회원의 경우 이메일이 전송된다.")
		@Test
		void should_sendEmail_when_invokeMethod() throws Exception {

			SendEmailRequest request = SendEmailRequest.builder()
				.email("email@email.com")
				.build();

			willDoNothing()
				.given(memberService)
				.sendEmailAuthCode(any(SendEmailServiceRequest.class));

			mockMvc.perform(post(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isAccepted(),
					jsonPath("$.status.message").value(
						SUCCESS_SEND_EMAIL_AUTH_CODE.getMessage())
				);
		}

		@DisplayName("이메일 형태가 올바르지 않을 경우 실패한다.")
		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = {"abc123", "abc@@asdf", "abc123@naver", "abc123@.com"})
		void should_fail_when_invalidEmail(String email) throws Exception {

			SendEmailRequest request = SendEmailRequest.builder()
				.email(email)
				.build();

			mockMvc.perform(post(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}
	}

	@DisplayName("비밀번호 변경 인증 코드 전송 시")
	@Nested
	class VerifyEmailAuthCode {

		private static final String REQUEST_URL = "/members/password/email/code";

		@DisplayName("올바른 코드를 요청하면 성공한다.")
		@Test
		void should_success_when_validCode() throws Exception {

			PasswordEmailVerifyRequest request = createRequest("ABC123");
			PasswordEmailVerifyServiceResponse response = PasswordEmailVerifyServiceResponse.builder()
				.uuid(UUID.randomUUID().toString())
				.build();

			given(memberService.verifyEmailAuthCode(any(PasswordEmailVerifyServiceRequest.class)))
				.willReturn(response);

			mockMvc.perform(post(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.data.uuid").value(response.getUuid())
				);
		}

		@DisplayName("code가 null이거나 형태가 올바르지 않으면 실패한다.")
		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = {"ABCD123", "aBC123", "******"})
		void should_fail_when_inValidCode(String code) throws Exception {

			PasswordEmailVerifyRequest request = createRequest(code);
			PasswordEmailVerifyServiceResponse response = PasswordEmailVerifyServiceResponse.builder()
				.uuid(UUID.randomUUID().toString())
				.build();

			given(memberService.verifyEmailAuthCode(any(PasswordEmailVerifyServiceRequest.class)))
				.willReturn(response);

			mockMvc.perform(post(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		@DisplayName("이메일 형태가 올바르지 않을 경우 실패한다.")
		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = {"abc123", "abc@@asdf", "abc123@naver", "abc123@.com"})
		void should_fail_when_invalidEmail(String email) throws Exception {

			PasswordEmailVerifyRequest request = PasswordEmailVerifyRequest.builder()
				.email(email)
				.build();

			mockMvc.perform(post(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		private PasswordEmailVerifyRequest createRequest(String code) {
			return PasswordEmailVerifyRequest.builder()
				.email("email@email.com")
				.code(code)
				.build();
		}
	}

	@DisplayName("비밀번호 재설정 시")
	@Nested
	class ResetPassword {

		private static final String REQUEST_URL = "/members/password";

		@DisplayName("올바른 요청이면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {
			PasswordResetRequest request = createRequest(UUID.randomUUID().toString(), "Abc123**");

			willDoNothing()
				.given(memberService)
				.resetPassword(any(PasswordResetServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(SUCCESS_UPDATE_PASSWORD.getMessage())
				);
		}

		@DisplayName("비밀번호가 null이거나 정규 표현식에 위배되면 실패한다.")
		@NullAndEmptySource
		@ParameterizedTest
		@ValueSource(strings = {"abc123**", "ABC123*", "ABC123***********", "ABC12345", "ABCD****"})
		void should_fail_when_invalidPassword(String password) throws Exception {
			PasswordResetRequest request = createRequest(UUID.randomUUID().toString(), password);

			willDoNothing()
				.given(memberService)
				.updatePassword(any(Long.class), any(PasswordUpdateServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		@DisplayName("이메일 형태가 올바르지 않을 경우 실패한다.")
		@ParameterizedTest
		@NullAndEmptySource
		@ValueSource(strings = {"abc123", "abc@@asdf", "abc123@naver", "abc123@.com"})
		void should_fail_when_invalidEmail(String email) throws Exception {

			PasswordResetRequest request = PasswordResetRequest.builder()
				.email(email)
				.build();

			mockMvc.perform(patch(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		@DisplayName("uuid가 null일 경우 실패한다.")
		@ParameterizedTest
		@NullSource
		void should_fail_when_invalidUUID(String uuid) throws Exception {

			PasswordResetRequest request = PasswordResetRequest.builder()
				.uuid(uuid)
				.build();

			mockMvc.perform(patch(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		private PasswordResetRequest createRequest(String uuid, String password) {
			return PasswordResetRequest.builder()
				.email("email@email.com")
				.uuid(uuid)
				.password(password)
				.build();
		}
	}

	@DisplayName("비밀번호 변경 시")
	@Nested
	class UpdatePassword {

		private static final String REQUEST_URL = "/members/{member_id}/password";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청이면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {
			PasswordUpdateRequest request = createRequest("Abc123**");

			willDoNothing()
				.given(memberService)
				.updatePassword(any(Long.class), any(PasswordUpdateServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(SUCCESS_UPDATE_PASSWORD.getMessage())
				);
		}

		@DisplayName("비밀번호가 null이거나 정규 표현식에 위배되면 실패한다.")
		@NullAndEmptySource
		@ParameterizedTest
		@ValueSource(strings = {"abc123**", "ABC123*", "ABC123***********", "ABC12345", "ABCD****"})
		void should_fail_when_invalidPassword(String password) throws Exception {
			PasswordUpdateRequest request = createRequest(password);

			willDoNothing()
				.given(memberService)
				.updatePassword(any(Long.class), any(PasswordUpdateServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		private PasswordUpdateRequest createRequest(String password) {
			return PasswordUpdateRequest.builder()
				.currentPassword("aBC123**")
				.newPassword(password)
				.build();
		}
	}

	@DisplayName("status 변경 시")
	@Nested
	class UpdateStatus {

		private static final String REQUEST_URL = "/members/{member_id}/status";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청이면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {

			StatusUpdateRequest request = createRequest(Status.AWAY);

			willDoNothing()
				.given(memberService)
				.updateStatus(any(Long.class), any(StatusUpdateServiceRequest.class));
			willDoNothing()
				.given(stompService)
				.updateConnStatus(any(User.class), any(Status.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(SUCCESS_UPDATE_STATUS.getMessage())
				);
		}

		@DisplayName("status가 null이면 실패한다.")
		@Test
		void should_fail_when_statusIsNull() throws Exception {

			StatusUpdateRequest request = createRequest(null);

			willDoNothing()
				.given(memberService)
				.updateStatus(any(Long.class), any(StatusUpdateServiceRequest.class));
			willDoNothing()
				.given(stompService)
				.updateConnStatus(any(User.class), any(Status.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		private StatusUpdateRequest createRequest(Status status) {
			return StatusUpdateRequest.builder()
				.status(status)
				.build();
		}
	}

	@DisplayName("introduction 변경 시")
	@Nested
	class UpdateIntroduction {

		private static final String REQUEST_URL = "/members/{member_id}/introductions";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청이면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {

			IntroductionUpdateRequest request = createRequest(true, "content");

			willDoNothing()
				.given(memberService)
				.updateIntroduction(any(Long.class),
					any(IntroductionUpdateServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(
						SUCCESS_UPDATE_INTRODUCTION.getMessage())
				);
		}

		@DisplayName("content또는 isMain이 null이면 실패한다.")
		@Test
		void should_fail_when_contentOrIsMainIsNull() throws Exception {

			IntroductionUpdateRequest request = createRequest(null, null);

			willDoNothing()
				.given(memberService)
				.updateIntroduction(any(Long.class),
					any(IntroductionUpdateServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		@DisplayName("content가 90자 초과이면 실패한다.")
		@Test
		void should_fail_when_contentLengthExceed90() throws Exception {

			IntroductionUpdateRequest request = createRequest(true, "a".repeat(91));

			willDoNothing()
				.given(memberService)
				.updateIntroduction(any(Long.class),
					any(IntroductionUpdateServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		private IntroductionUpdateRequest createRequest(Boolean isMain, String content) {
			return IntroductionUpdateRequest.builder()
				.introductions(List.of(IntroductionEntry.builder()
					.id(1L)
					.isMain(isMain)
					.content(content)
					.build()))
				.build();
		}
	}

	@DisplayName("preferGameMode 변경 시")
	@Nested
	class UpdatePreferGameMode {

		private static final String REQUEST_URL = "/members/{member_id}/prefer-game-mode";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청이면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {

			PreferGameModeUpdateRequest request = createRequest(GameMode.RANK_SOLO);

			willDoNothing()
				.given(memberService)
				.updatePreferGameMode(any(PreferGameModeUpdateServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(
						SUCCESS_UPDATE_PREFER_GAME_MODE.getMessage())
				);
		}

		@DisplayName("preferGameMode가 null이면 실패한다.")
		@Test
		void should_fail_when_preferGameModeIsNull() throws Exception {

			PreferGameModeUpdateRequest request = createRequest(null);

			willDoNothing()
				.given(memberService)
				.updatePreferGameMode(any(PreferGameModeUpdateServiceRequest.class));

			mockMvc.perform(patch(REQUEST_URL, MEMBER_ID)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}

		private PreferGameModeUpdateRequest createRequest(GameMode gameMode) {
			return PreferGameModeUpdateRequest.builder()
				.preferGameModes(List.of(
					PreferGameModeEntry.builder()
						.gameMode(gameMode)
						.build()
				))
				.build();
		}
	}

	@DisplayName("oauth 연동 해제 시")
	@Nested
	class DeleteOauthInfo {

		private static final String REQUEST_URL = "/members/{member_id}/oauth";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("올바른 요청을 하면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {

			willDoNothing()
				.given(memberService)
				.deleteOauthInfo(any(Provider.class));

			mockMvc.perform(delete(REQUEST_URL, MEMBER_ID)
					.param("provider", Provider.KAKAO.name()))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(
						SUCCESS_DISCONNECT_OAUTH.getMessage())
				);
		}

		@DisplayName("provider가 null이면 실패한다.")
		@Test
		void should_fail_when_providerIsNull() throws Exception {
			String message = String.format(MISSING_REQUEST_PARAMETER, "provider");

			willDoNothing()
				.given(memberService)
				.deleteOauthInfo(any(Provider.class));

			mockMvc.perform(delete(REQUEST_URL, MEMBER_ID))
				.andExpectAll(
					status().isNotFound(),
					jsonPath("$.status.message").value(message)
				);
		}
	}

	@DisplayName("로그아웃 시")
	@Nested
	class Logout {

		private static final String REQUEST_URL = "/members/{member_id}/logout";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("인증된 상태로 요청하면 성공한다.")
		@Test
		void should_success_when_authenticated() throws Exception {

			willDoNothing()
				.given(memberService)
				.logout();

			mockMvc.perform(delete(REQUEST_URL, MEMBER_ID))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(SUCCESS_LOGOUT.getMessage())
				);
		}
	}

	@DisplayName("회원탈퇴 시")
	@Nested
	class Withdrawal {

		private static final String REQUEST_URL = "/members/{member_id}";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("인증된 상태로 요청하면 성공한다.")
		@Test
		void should_success_when_authenticated() throws Exception {

			willDoNothing()
				.given(memberService)
				.withdrawal();

			mockMvc.perform(delete(REQUEST_URL, MEMBER_ID))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(SUCCESS_WITHDRAWAL.getMessage())
				);
		}
	}

	@DisplayName("다른 회원 프로필 조회 시")
	@Nested
	class GetOtherProfile {

		private static final String REQUEST_URL = "/members/{member_id}";
		private static final Long MEMBER_ID = 1L;

		@DisplayName("인증된 상태로 요청시 성공한다.")
		@Test
		void should_success_when_authenticated() throws Exception {

			OtherProfileServiceResponse response = OtherProfileServiceResponse.builder()
				.schedules(List.of(
					ScheduleEntry.builder()
						.dayOfWeek(DayOfWeek.SUNDAY)
						.startTime(0)
						.endTime(24)
						.build()))
				.mainIntroduction("소개글")
				.preferGameModes(List.of(
					PreferGameModeEntry.builder()
						.gameMode(GameMode.RANK_SOLO)
						.build()))
				.build();

			given(memberReadService.findOtherById(any(Long.class)))
				.willReturn(response);

			mockMvc.perform(get(REQUEST_URL, MEMBER_ID))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.data.schedules[0].dayOfWeek")
						.value(response.getSchedules().get(0).getDayOfWeek().name()),
					jsonPath("$.data.schedules[0].startTime")
						.value(response.getSchedules().get(0).getStartTime()),
					jsonPath("$.data.schedules[0].endTime")
						.value(response.getSchedules().get(0).getEndTime()),
					jsonPath("$.data.mainIntroduction")
						.value(response.getMainIntroduction()),
					jsonPath("$.data.preferGameModes[0].gameMode")
						.value(response.getPreferGameModes().get(0).getGameMode().name())
				);
		}
	}
}
