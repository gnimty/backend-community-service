package com.gnimty.communityapiserver.controller.member;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_SEND_EMAIL_AUTH_CODE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_SIGN_UP;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_VERIFY_EMAIL;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.HEADER_NOT_FOUND;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gnimty.communityapiserver.controller.ControllerTestSupport;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.EmailAuthRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.EmailVerifyRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.LoginRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.OauthLoginRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.SignupRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.response.AuthToken;
import com.gnimty.communityapiserver.domain.member.service.dto.request.EmailAuthServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.EmailVerifyServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.LoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.OauthLoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SignupServiceRequest;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

public class AuthControllerTest extends ControllerTestSupport {

    @BeforeEach
    void setUp() {
        given(tokenAuthInterceptor.preHandle(
            any(HttpServletRequest.class),
            any(HttpServletResponse.class),
            any(Object.class)))
            .willReturn(true);
    }

    @DisplayName("회원가입 시")
    @Nested
    class Signup {

        private static final String REQUEST_URL = "/auth/signup";

        @BeforeEach
        void setUp() {
            willDoNothing()
                .given(authService)
                .signup(any(SignupServiceRequest.class));
        }

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            SignupRequest request = createRequest("email@email.com", "Zas123**", true);

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.status.message").value(SUCCESS_SIGN_UP.getMessage())
                );
        }

        @DisplayName("이메일 형태가 올바르지 않으면 실패한다.")
        @NullAndEmptySource
        @ParameterizedTest
        @ValueSource(strings = {"emailemail.com", "email@email", "email"})
        void should_fail_when_invalidEmail(String email) throws Exception {

            SignupRequest request = createRequest(email, "Zas123**", true);

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        @DisplayName("비밀번호 형태가 올바르지 않으면 실패한다.")
        @NullAndEmptySource
        @ParameterizedTest
        @ValueSource(strings = {"password", "password123", "Password123", "Pa12*",
            "ppppppppppppPPPPPP123**"})
        void should_fail_when_invalidPassword(String password) throws Exception {

            SignupRequest request = createRequest("email@email.com", password, true);

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        @DisplayName("agreeTerms가 false이면 실패 한다.")
        @Test
        void should_fail_when_invalidAgreeTerms() throws Exception {

            SignupRequest request = createRequest("email@email.com", "Zas123**", false);

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private SignupRequest createRequest(String email, String password, boolean agreeTerms) {
            return SignupRequest.builder()
                .email(email)
                .password(password)
                .agreeTerms(agreeTerms)
                .build();
        }
    }

    @DisplayName("로그인 시")
    @Nested
    class Login {

        private static final String REQUEST_URL = "/auth/login";
        private AuthToken authToken;

        @BeforeEach
        void setUp() {
            authToken = AuthToken.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
            given(authService.login(any(LoginServiceRequest.class)))
                .willReturn(authToken);
        }

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            LoginRequest request = createRequest();

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.accessToken").value(authToken.getAccessToken()),
                    jsonPath("$.data.refreshToken").value(authToken.getRefreshToken())
                );
        }

        private LoginRequest createRequest() {
            return LoginRequest.builder()
                .email("email@email.com")
                .password("Zas123**")
                .build();
        }
    }

    @DisplayName("카카오 로그인 시")
    @Nested
    class KakaoLogin {

        private static final String REQUEST_URL = "/oauth/kakao";
        private AuthToken authToken;

        @BeforeEach
        void setUp() {
            authToken = AuthToken.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
            given(authService.kakaoLogin(any(OauthLoginServiceRequest.class)))
                .willReturn(authToken);
        }

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            OauthLoginRequest request = createRequest("authCode");

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.accessToken").value(authToken.getAccessToken()),
                    jsonPath("$.data.refreshToken").value(authToken.getRefreshToken())
                );
        }

        @DisplayName("authCode가 null이면 실패한다.")
        @Test
        void should_fail_when_authCodeIsNull() throws Exception {

            OauthLoginRequest request = createRequest(null);

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private OauthLoginRequest createRequest(String authCode) {
            return OauthLoginRequest.builder()
                .authCode(authCode)
                .redirectUri("redirectUri")
                .build();
        }
    }

    @DisplayName("구글 로그인 시")
    @Nested
    class GoogleLogin {

        private static final String REQUEST_URL = "/oauth/google";
        private AuthToken authToken;

        @BeforeEach
        void setUp() {
            authToken = AuthToken.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
            given(authService.googleLogin(any(OauthLoginServiceRequest.class)))
                .willReturn(authToken);
        }

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            OauthLoginRequest request = createRequest("authCode");

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.accessToken").value(authToken.getAccessToken()),
                    jsonPath("$.data.refreshToken").value(authToken.getRefreshToken())
                );
        }

        @DisplayName("authCode가 null이면 실패한다.")
        @Test
        void should_fail_when_authCodeIsNull() throws Exception {

            OauthLoginRequest request = createRequest(null);

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private OauthLoginRequest createRequest(String authCode) {
            return OauthLoginRequest.builder()
                .authCode(authCode)
                .redirectUri("redirectUri")
                .build();
        }
    }

    @DisplayName("이메일 인증 메일 전송 시")
    @Nested
    class SendEmailAuthCode {

        private static final String REQUEST_URL = "/auth/email";

        @BeforeEach
        void setUp() {
            willDoNothing()
                .given(authService)
                .sendEmailAuthCode(any(EmailAuthServiceRequest.class));
        }

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            EmailAuthRequest request = createRequest("email@email.com");

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isAccepted(),
                    jsonPath("$.status.message").value(SUCCESS_SEND_EMAIL_AUTH_CODE.getMessage())
                );
        }

        @DisplayName("이메일 형태가 올바르지 않으면 실패한다.")
        @NullAndEmptySource
        @ParameterizedTest
        @ValueSource(strings = {"emailemail.com", "email@email", "email"})
        void should_fail_when_invalidEmail(String email) throws Exception {

            EmailAuthRequest request = createRequest(email);

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private EmailAuthRequest createRequest(String email) {
            return EmailAuthRequest.builder()
                .email(email)
                .build();
        }
    }

    @DisplayName("이메일 인증 코드 검증 시")
    @Nested
    class VerifyEmailAuthCode {

        private static final String REQUEST_URL = "/auth/email/code";

        @BeforeEach
        void setUp() {
            willDoNothing()
                .given(authService)
                .verifyEmailAuthCode(any(EmailVerifyServiceRequest.class));
        }

        @DisplayName("올바른 인증 코드를 입력 시 성공한다.")
        @Test
        void should_success_when_validAuthCode() throws Exception {

            EmailVerifyRequest request = createRequest("ABC123");

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status.message").value(SUCCESS_VERIFY_EMAIL.getMessage())
                );
        }

        @DisplayName("인증 코드의 형태가 올바르지 않으면 실패한다.")
        @NullAndEmptySource
        @ParameterizedTest
        @ValueSource(strings = {"abc123", "ABC12", "ABC12*"})
        void should_fail_when_inValidAuthCode(String authCode) throws Exception {

            EmailVerifyRequest request = createRequest(authCode);

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private EmailVerifyRequest createRequest(String code) {
            return EmailVerifyRequest.builder()
                .email("email@email.com")
                .code(code)
                .build();
        }
    }

    @DisplayName("토큰 재발급 시")
    @Nested
    class TokenRefresh {

        private static final String REQUEST_URL = "/auth/refresh";
        private AuthToken authToken;

        @BeforeEach
        void setUp() {
            authToken = AuthToken.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
            given(authService.tokenRefresh(any(String.class)))
                .willReturn(authToken);
        }

        @DisplayName("올바른 리프레시 토큰을 입력 시 성공한다.")
        @Test
        void should_success_when_validRefreshToken() throws Exception {

            mockMvc.perform(get(REQUEST_URL)
                    .header("RefreshToken", "token"))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.accessToken").value(authToken.getAccessToken()),
                    jsonPath("$.data.refreshToken").value(authToken.getRefreshToken())
                );
        }

        @DisplayName("헤더가 없을 시 실패한다.")
        @Test
        void should_fail_when_refreshTokenIsNull() throws Exception {

            mockMvc.perform(get(REQUEST_URL))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(HEADER_NOT_FOUND)
                );
        }
    }
}
