package com.gnimty.communityapiserver.service.member;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ALREADY_REGISTERED_EMAIL;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.INVALID_LOGIN;
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
import com.gnimty.communityapiserver.domain.member.service.dto.request.LoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SignupServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.utils.GoogleOauthUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.KakaoOauthUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.MailSenderUtil;
import com.gnimty.communityapiserver.domain.oauthinfo.repository.OauthInfoRepository;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.constant.Auth;
import com.gnimty.communityapiserver.global.constant.KeyPrefix;
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

	@DisplayName("회원 가입 시")
	@Nested
	class Signup {

		@Mock
		private ValueOperations<String, String> valueOperations;

		@BeforeEach
		void setUp() {
			willDoNothing()
				.given(memberReadService)
				.throwIfExistByEmail(any(String.class));
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			given(valueOperations.get(any()))
				.willReturn("verify");
			given(redisTemplate.delete(any(String.class)))
				.willReturn(true);
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
		@Mock
		private ValueOperations<String, String> valueOperations;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(createMember());
			given(memberReadService.findByEmailOrElseThrow(any(String.class),
				any(BaseException.class)))
				.willReturn(member);
			given(passwordEncoder.matches(any(CharSequence.class), any(String.class)))
				.willReturn(true);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			willDoNothing()
				.given(valueOperations)
				.set(any(String.class), any(String.class));
			given(redisTemplate.expire(any(String.class), any(Long.class), any(TimeUnit.class)))
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

			given(jwtProvider.generateToken(any(Long.class), eq(Auth.ACCESS_TOKEN_EXPIRATION.getExpiration()), any(String.class)))
				.willReturn(authToken.getAccessToken().replaceAll(Auth.BEARER.getContent(), ""));
			given(jwtProvider.generateToken(any(Long.class), eq(Auth.REFRESH_TOKEN_EXPIRATION.getExpiration()), any(String.class)))
				.willReturn(authToken.getRefreshToken().replaceAll(Auth.BEARER.getContent(), ""));

			AuthToken login = authService.login(request);

			assertThat(login.getAccessToken()).isEqualTo(authToken.getAccessToken());
			assertThat(login.getRefreshToken()).isEqualTo(authToken.getRefreshToken());
		}

		@DisplayName("존재하지 않는 email을 요청하면 실패한다.")
		@Test
		void should_fail_when_notExistEmail() {
			LoginServiceRequest request = createRequest();
			BaseException exception = new BaseException(INVALID_LOGIN);

			given(memberReadService.findByEmailOrElseThrow(any(String.class), any(BaseException.class)))
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
				.accessToken(Auth.BEARER.getContent() + "accessToken")
				.refreshToken(Auth.BEARER.getContent() + "refreshToken")
				.build();
		}
	}

	@DisplayName("카카오 로그인 시")
	@Nested
	class KakaoLogin {

	}

	@DisplayName("구글 로그인 시")
	@Nested
	class GoogleLogin {

	}

	@DisplayName("이메일 인증 코드 전송 시")
	@Nested
	class SendEmailAuthCode {

	}

	@DisplayName("이메일 인증 코드 확인 시")
	@Nested
	class VerifyEmailAuthCode {

	}

	@DisplayName("토큰 재발급 시")
	@Nested
	class TokenRefresh {

	}
}
