package com.gnimty.communityapiserver.service.member;

import static com.gnimty.communityapiserver.global.constant.Auth.BEARER;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.INVALID_EMAIL_AUTH_CODE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.INVALID_LOGIN;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.TOKEN_INVALID;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.UNAUTHORIZED_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.gnimty.communityapiserver.domain.member.controller.dto.response.AuthToken;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.member.service.AuthService;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.domain.member.service.dto.request.EmailAuthServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.EmailVerifyServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.LoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.OauthLoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SignupServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.utils.GoogleOauthUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.KakaoOauthUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.MailSenderUtil;
import com.gnimty.communityapiserver.domain.oauthinfo.entity.OauthInfo;
import com.gnimty.communityapiserver.domain.oauthinfo.repository.OauthInfoRepository;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.constant.Auth;
import com.gnimty.communityapiserver.global.constant.KeyPrefix;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

public class AuthServiceTest extends ServiceTestSupport {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OauthInfoRepository oauthInfoRepository;
    @MockBean
    private MemberReadService memberReadService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private StringRedisTemplate redisTemplate;
    @MockBean
    private KakaoOauthUtil kakaoOauthUtil;
    @MockBean
    private GoogleOauthUtil googleOauthUtil;
    @MockBean
    private MailSenderUtil mailSenderUtil;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        given(redisTemplate.opsForValue())
            .willReturn(valueOperations);
        given(redisTemplate.delete(any(String.class)))
            .willReturn(true);
        willDoNothing()
            .given(valueOperations)
            .set(any(String.class), any(String.class));
        given(redisTemplate.expire(any(String.class), any(Long.class), any(TimeUnit.class)))
            .willReturn(true);
    }

    @DisplayName("회원 가입 시")
    @Nested
    class Signup {

        @BeforeEach
        void setUp() {
            willDoNothing()
                .given(memberReadService)
                .throwIfExistByEmail(any(String.class));
            given(valueOperations.get(any()))
                .willReturn("verify");
        }

        @AfterEach
        void tearDown() {
            memberRepository.deleteAllInBatch();
        }

        @DisplayName("이메일 인증을 완료하고, form 회원가입 이력이 없는 이메일일 경우, 회원가입 되고, redis의 key가 삭제되며, id로 임시 닉네임이 만들어진다.")
        @Test
        void should_signupAndDeleteRedisKeyAndTemporaryNickname_when_verifyEmailAndNotFormLogin() {
            SignupServiceRequest request = createRequest();

            given(passwordEncoder.encode(any(CharSequence.class)))
                .willReturn(request.getPassword());

            authService.signup(request);
            Optional<Member> optionalMember = memberRepository.findByEmail(request.getEmail());
            assertThat(optionalMember).isPresent();
            Member member = optionalMember.get();
            assertThat(member.getEmail()).isEqualTo(request.getEmail());
            assertThat(member.getPassword()).isEqualTo(request.getPassword());
            assertThat(member.getRsoLinked()).isFalse();
            assertThat(member.getFavoriteChampionID()).isNull();
            assertThat(member.getNickname()).contains(KeyPrefix.NICKNAME.getPrefix(),
                String.valueOf(member.getId()));
            assertThat(member.getStatus()).isEqualTo(Status.OFFLINE);
            assertThat(member.getUpCount()).isEqualTo(0);

            then(redisTemplate)
                .should(times(1))
                .delete(any(String.class));
            then(passwordEncoder)
                .should(times(1))
                .encode(any(CharSequence.class));
        }

        @DisplayName("이메일 인증을 완료하지 않았을 경우, 예외를 반환한다.")
        @Test
        void should_throwException_when_notVerifyEmail() {
            SignupServiceRequest request = createRequest();
            BaseException exception = new BaseException(UNAUTHORIZED_EMAIL);

            given(valueOperations.get(any()))
                .willReturn(null);

            assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        @DisplayName("이미 존재하는 이메일일 경우, 예외를 반환한다.")
        @Test
        void should_throwException_when_alreadyExistEmail() {
            SignupServiceRequest request = createRequest();
            BaseException exception = new BaseException(ALREADY_REGISTERED_EMAIL);

            willThrow(exception)
                .given(memberReadService)
                .throwIfExistByEmail(any(String.class));

            assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        private SignupServiceRequest createRequest() {
            return SignupServiceRequest.builder()
                .email("email@email.com")
                .password("Zas123**")
                .agreeTerms(true)
                .build();
        }
    }

    @DisplayName("로그인 시")
    @Nested
    class Login {

        private Member member;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(createMember());
            given(memberReadService.findByEmailOrElseThrow(any(String.class),
                any(BaseException.class)))
                .willReturn(member);
            given(passwordEncoder.matches(any(CharSequence.class), any(String.class)))
                .willReturn(true);
        }

        @AfterEach
        void tearDown() {
            memberRepository.deleteAllInBatch();
        }

        @DisplayName("올바른 이메일, 비밀번호를 입력하면 로그인에 성공한다.")
        @Test
        void should_successLogin_when_validEmailAndPassword() {
            LoginServiceRequest request = createRequest();
            AuthToken authToken = createAuthToken();

            given(jwtProvider.generateToken(any(Long.class),
                eq(Auth.ACCESS_TOKEN_EXPIRATION.getExpiration()), any(String.class)))
                .willReturn(authToken.getAccessToken().replaceAll(BEARER.getContent(), ""));
            given(jwtProvider.generateToken(any(Long.class),
                eq(Auth.REFRESH_TOKEN_EXPIRATION.getExpiration()), any(String.class)))
                .willReturn(authToken.getRefreshToken().replaceAll(BEARER.getContent(), ""));

            AuthToken login = authService.login(request);

            assertThat(login.getAccessToken()).isEqualTo(authToken.getAccessToken());
            assertThat(login.getRefreshToken()).isEqualTo(authToken.getRefreshToken());
        }

        @DisplayName("존재하지 않는 email을 요청하면 실패한다.")
        @Test
        void should_fail_when_notExistEmail() {
            LoginServiceRequest request = createRequest();
            BaseException exception = new BaseException(INVALID_LOGIN);

            given(memberReadService.findByEmailOrElseThrow(any(String.class),
                any(BaseException.class)))
                .willThrow(exception);

            assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        @DisplayName("비밀번호가 올바르지 않으면 실패한다.")
        @Test
        void should_fail_when_invalidPassword() {
            LoginServiceRequest request = createRequest();
            BaseException exception = new BaseException(INVALID_LOGIN);

            given(passwordEncoder.matches(any(CharSequence.class), any(String.class)))
                .willReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        private LoginServiceRequest createRequest() {
            return LoginServiceRequest.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .build();
        }

        private Member createMember() {
            return Member.builder()
                .email("email@email.com")
                .password("password")
                .rsoLinked(false)
                .favoriteChampionID(1L)
                .status(Status.OFFLINE)
                .upCount(1L)
                .nickname("nickname")
                .build();
        }

        private AuthToken createAuthToken() {
            return AuthToken.builder()
                .accessToken(BEARER.getContent() + "accessToken")
                .refreshToken(BEARER.getContent() + "refreshToken")
                .build();
        }
    }

    @DisplayName("카카오 로그인 시")
    @Nested
    class KakaoLogin {

        @BeforeEach
        void setUp() {
            given(jwtProvider.generateToken(any(Long.class), any(Long.class), any(String.class)))
                .willReturn("token");
            given(kakaoOauthUtil.getKakaoUserEmail(any(String.class)))
                .willReturn("email@email.com");
        }

        @AfterEach
        void tearDown() {
            oauthInfoRepository.deleteAllInBatch();
            memberRepository.deleteAllInBatch();
        }

        @DisplayName("OauthInfo가 존재할 경우, token을 생성하여 응답한다.")
        @Test
        void should_generateTokenPair_when_oauthInfoExist() {
            Member member = memberRepository.save(Member.builder()
                .email(null)
                .nickname("name")
                .status(Status.ONLINE)
                .password("password")
                .rsoLinked(false)
                .upCount(0L)
                .build()
            );
            oauthInfoRepository.save(OauthInfo.builder()
                .email("email@email.com")
                .member(member)
                .provider(Provider.KAKAO)
                .build()
            );

            OauthLoginServiceRequest request = OauthLoginServiceRequest.builder()
                .authCode("authCode")
                .build();

            AuthToken authToken = authService.kakaoLogin(request);

            assertThat(authToken.getAccessToken().replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");
            assertThat(authToken.getRefreshToken().replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");
            assertThat(oauthInfoRepository.findAll()).hasSize(1);
            assertThat(memberRepository.findAll()).hasSize(1);
        }

        @Transactional
        @DisplayName("OauthInfo가 존재하지 않을 경우, member를 생성하여 응답한다.")
        @Test
        void should_generateMember_when_oauthInfoNotExist() {
            OauthLoginServiceRequest request = OauthLoginServiceRequest.builder()
                .authCode("authCode")
                .build();

            AuthToken authToken = authService.kakaoLogin(request);

            assertThat(authToken.getAccessToken()
                .replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");
            assertThat(authToken.getRefreshToken().replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");

            Member member = memberRepository.findAll().get(0);
            OauthInfo oauthInfo = oauthInfoRepository.findAll().get(0);

            assertThat(member.getEmail()).isNull();
            assertThat(oauthInfo.getMember()).isEqualTo(member);
            assertThat(oauthInfo.getProvider()).isEqualTo(Provider.KAKAO);
            assertThat(oauthInfo.getEmail()).isEqualTo("email@email.com");
        }
    }

    @DisplayName("구글 로그인 시")
    @Nested
    class GoogleLogin {

        @BeforeEach
        void setUp() {
            given(jwtProvider.generateToken(any(Long.class), any(Long.class), any(String.class)))
                .willReturn("token");
            given(googleOauthUtil.getGoogleUserEmail(any(String.class)))
                .willReturn("email@email.com");
        }

        @AfterEach
        void tearDown() {
            oauthInfoRepository.deleteAllInBatch();
            memberRepository.deleteAllInBatch();
        }

        @DisplayName("OauthInfo가 존재할 경우, token을 생성하여 응답한다.")
        @Test
        void should_generateTokenPair_when_oauthInfoExist() {
            Member member = memberRepository.save(Member.builder()
                .email(null)
                .nickname("name")
                .status(Status.ONLINE)
                .password("password")
                .rsoLinked(false)
                .upCount(0L)
                .build()
            );
            oauthInfoRepository.save(OauthInfo.builder()
                .email("email@email.com")
                .member(member)
                .provider(Provider.GOOGLE)
                .build()
            );

            OauthLoginServiceRequest request = OauthLoginServiceRequest.builder()
                .authCode("authCode")
                .build();

            AuthToken authToken = authService.googleLogin(request);

            assertThat(authToken.getAccessToken().replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");
            assertThat(authToken.getRefreshToken().replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");
            assertThat(oauthInfoRepository.findAll()).hasSize(1);
            assertThat(memberRepository.findAll()).hasSize(1);
        }

        @Transactional
        @DisplayName("OauthInfo가 존재하지 않을 경우, member를 생성하여 응답한다.")
        @Test
        void should_generateMember_when_oauthInfoNotExist() {
            OauthLoginServiceRequest request = OauthLoginServiceRequest.builder()
                .authCode("authCode")
                .build();

            AuthToken authToken = authService.googleLogin(request);

            assertThat(authToken.getAccessToken()
                .replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");
            assertThat(authToken.getRefreshToken().replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");

            Member member = memberRepository.findAll().get(0);
            OauthInfo oauthInfo = oauthInfoRepository.findAll().get(0);

            assertThat(member.getEmail()).isNull();
            assertThat(oauthInfo.getMember()).isEqualTo(member);
            assertThat(oauthInfo.getProvider()).isEqualTo(Provider.GOOGLE);
            assertThat(oauthInfo.getEmail()).isEqualTo("email@email.com");
        }
    }

    @DisplayName("이메일 인증 코드 전송 시")
    @Nested
    class SendEmailAuthCode {

        @DisplayName("이메일 전송 시 코드가 저장되고 만료시간이 설정된다.")
        @Test
        void should_savedAndExpiredCode_when_emailSend() {
            EmailAuthServiceRequest request = EmailAuthServiceRequest.builder()
                .email("email@email.com")
                .build();

            willDoNothing()
                .given(mailSenderUtil)
                .sendEmail(any(String.class), any(String.class), any(String.class),
                    any(String.class), any(String.class));

            authService.sendEmailAuthCode(request);

            then(mailSenderUtil)
                .should(times(1))
                .sendEmail(any(String.class), any(String.class), any(String.class),
                    any(String.class), any(String.class));
            then(valueOperations)
                .should(times(1))
                .set(any(String.class), any(String.class));
            then(redisTemplate)
                .should(times(1))
                .expire(any(String.class), any(Long.class), any(TimeUnit.class));
        }
    }

    @DisplayName("이메일 인증 코드 확인 시")
    @Nested
    class VerifyEmailAuthCode {

        @DisplayName("올바른 인증 코드를 입력하면 성공한다.")
        @Test
        void should_success_when_validAuthCode() {
            EmailVerifyServiceRequest request = EmailVerifyServiceRequest.builder()
                .email("email@email.com")
                .code("code")
                .build();

            given(valueOperations.get(any(String.class)))
                .willReturn(request.getCode());

            authService.verifyEmailAuthCode(request);

            then(redisTemplate)
                .should(times(1))
                .expire(any(String.class), any(Long.class), any(TimeUnit.class));
            then(valueOperations)
                .should(times(1))
                .set(any(String.class), any(String.class));
            then(redisTemplate)
                .should(times(1))
                .delete(any(String.class));
        }

        @DisplayName("올바르지 않은 인증 코드를 입력하면 실패한다.")
        @Test
        void should_fail_when_invalidAuthCode() {
            EmailVerifyServiceRequest request = EmailVerifyServiceRequest.builder()
                .email("email@email.com")
                .code("code")
                .build();
            BaseException exception = new BaseException(INVALID_EMAIL_AUTH_CODE);

            given(valueOperations.get(any(String.class)))
                .willReturn("code123");

            assertThatThrownBy(() -> authService.verifyEmailAuthCode(request))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());

            then(valueOperations)
                .should(times(1))
                .get(any(String.class));
        }
    }

    @DisplayName("토큰 재발급 시")
    @Nested
    class TokenRefresh {

        @DisplayName("저장된 refresh token과 일치하는 토큰을 요청하면 성공한다.")
        @Test
        void should_success_when_requestValidRefreshToken() {
            String refreshToken = "refreshToken";

            given(valueOperations.get(any(String.class)))
                .willReturn(refreshToken);
            given(jwtProvider.getIdByToken(any(String.class)))
                .willReturn(1L);
            given(jwtProvider.generateToken(any(Long.class), any(Long.class), any(String.class)))
                .willReturn("token");

            AuthToken authToken = authService.tokenRefresh(refreshToken);

            assertThat(authToken.getAccessToken().replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");
            assertThat(authToken.getAccessToken().replaceAll(BEARER.getContent(), ""))
                .isEqualTo("token");
        }

        @DisplayName("저장된 refresh token과 일치하지 않는 토큰을 요청하면 실패한다.")
        @Test
        void should_fail_when_requestInvalidRefreshToken() {
            String refreshToken = "invalidRefreshToken";
            BaseException exception = new BaseException(TOKEN_INVALID);

            given(valueOperations.get(any(String.class)))
                .willReturn("token");
            given(jwtProvider.getIdByToken(any(String.class)))
                .willReturn(1L);
            given(jwtProvider.generateToken(any(Long.class), any(Long.class), any(String.class)))
                .willReturn("token");

            assertThatThrownBy(() -> authService.tokenRefresh(refreshToken))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }
    }
}
