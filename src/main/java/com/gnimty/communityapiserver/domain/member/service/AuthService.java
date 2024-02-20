package com.gnimty.communityapiserver.domain.member.service;

import static com.gnimty.communityapiserver.global.constant.Auth.ACCESS_TOKEN_EXPIRATION;
import static com.gnimty.communityapiserver.global.constant.Auth.EMAIL_SUBJECT;
import static com.gnimty.communityapiserver.global.constant.Auth.REFRESH_TOKEN_EXPIRATION;
import static com.gnimty.communityapiserver.global.constant.Auth.SUBJECT_ACCESS_TOKEN;
import static com.gnimty.communityapiserver.global.constant.Auth.SUBJECT_REFRESH_TOKEN;
import static com.gnimty.communityapiserver.global.constant.Bound.INITIAL_COUNT;
import static com.gnimty.communityapiserver.global.constant.Bound.RANDOM_CODE_LENGTH;
import static com.gnimty.communityapiserver.global.constant.CacheType.REFRESH_TOKEN;
import static com.gnimty.communityapiserver.global.constant.CacheType.SIGNUP_EMAIL_CODE;
import static com.gnimty.communityapiserver.global.constant.CacheType.SIGNUP_VERIFIED;
import static com.gnimty.communityapiserver.global.constant.CommonStringType.SIGNUP_EMAIL_BANNER;
import static com.gnimty.communityapiserver.global.constant.CommonStringType.SIGNUP_EMAIL_TEMPLATE;
import static com.gnimty.communityapiserver.global.constant.CommonStringType.VERIFY_SIGNUP;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.EMAIL;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.NICKNAME;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.REFRESH;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.SIGNUP;
import static com.gnimty.communityapiserver.global.utils.CacheService.getCacheKey;

import com.gnimty.communityapiserver.domain.member.controller.dto.response.AuthToken;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
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
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.utils.CacheService;
import com.gnimty.communityapiserver.global.utils.RandomCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

	private final MemberRepository memberRepository;
	private final OauthInfoRepository oauthInfoRepository;
	private final MemberReadService memberReadService;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final KakaoOauthUtil kakaoOauthUtil;
	private final GoogleOauthUtil googleOauthUtil;
	private final MailSenderUtil mailSenderUtil;
	private final CacheService cacheService;

	public void signup(SignupServiceRequest request) {
		memberReadService.throwIfExistByEmail(request.getEmail());
		String verify = cacheService.get(SIGNUP_VERIFIED, getCacheKey(SIGNUP, request.getEmail()));
		if (verify == null) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_EMAIL);
		}

		Member member = Member.builder()
			.rsoLinked(false)
			.email(request.getEmail())
			.password(encodePassword(request.getPassword()))
			.favoriteChampionID(null)
			.status(Status.OFFLINE)
			.upCount((long) INITIAL_COUNT.getValue())
			.build();

		memberRepository.save(member);
		member.updateNickname(generateTemporaryNickname(member.getId()));

		cacheService.evict(SIGNUP_VERIFIED, getCacheKey(SIGNUP, request.getEmail()));
	}

	public AuthToken login(LoginServiceRequest request) {
		Member member = memberReadService.findByEmailOrElseThrow(request.getEmail(),
			new BaseException(ErrorCode.INVALID_LOGIN));
		throwIfMismatchPassword(request.getPassword(), member.getPassword());

		AuthToken authToken = generateTokenPair(member.getId());
		cacheService.put(REFRESH_TOKEN, getCacheKey(REFRESH, member.getId().toString()), authToken.getRefreshToken());
		return authToken;
	}

	public AuthToken kakaoLogin(OauthLoginServiceRequest request) {
		String userEmail = kakaoOauthUtil.getKakaoUserEmail(request.getAuthCode(), request.getRedirectUri());

		Member member = oauthInfoRepository.findByEmail(userEmail)
			.map(OauthInfo::getMember)
			.orElseGet(() -> createMemberByEmail(userEmail, Provider.KAKAO));
		AuthToken authToken = generateTokenPair(member.getId());
		cacheService.put(REFRESH_TOKEN, getCacheKey(REFRESH, member.getId().toString()), authToken.getRefreshToken());
		member.updateNickname(generateTemporaryNickname(member.getId()));
		return authToken;
	}

	public AuthToken googleLogin(OauthLoginServiceRequest request) {
		String googleUserEmail = googleOauthUtil.getGoogleUserEmail(request.getAuthCode(), request.getRedirectUri());

		Member member = oauthInfoRepository.findByEmail(googleUserEmail)
			.map(OauthInfo::getMember)
			.orElseGet(() -> createMemberByEmail(googleUserEmail, Provider.GOOGLE));
		AuthToken authToken = generateTokenPair(member.getId());
		cacheService.put(REFRESH_TOKEN, getCacheKey(REFRESH, member.getId().toString()), authToken.getRefreshToken());
		member.updateNickname(generateTemporaryNickname(member.getId()));
		return authToken;
	}

	public void sendEmailAuthCode(EmailAuthServiceRequest request) {
		String code = RandomCodeGenerator.generateCodeByLength(RANDOM_CODE_LENGTH.getValue());
		mailSenderUtil.sendEmail(EMAIL_SUBJECT.getContent(), request.getEmail(), code,
			SIGNUP_EMAIL_TEMPLATE.getValue(), SIGNUP_EMAIL_BANNER.getValue());
		cacheService.put(SIGNUP_EMAIL_CODE, getCacheKey(EMAIL, request.getEmail()), code);
	}

	public void verifyEmailAuthCode(EmailVerifyServiceRequest request) {
		String emailAuthKey = getCacheKey(EMAIL, request.getEmail());
		String signupKey = getCacheKey(SIGNUP, request.getEmail());
		String savedCode = cacheService.get(SIGNUP_EMAIL_CODE, emailAuthKey);

		if (!request.getCode().equals(savedCode)) {
			throw new BaseException(ErrorCode.INVALID_EMAIL_AUTH_CODE);
		}

		cacheService.put(SIGNUP_VERIFIED, signupKey, VERIFY_SIGNUP.getValue());
		cacheService.evict(SIGNUP_EMAIL_CODE, emailAuthKey);
	}

	public AuthToken tokenRefresh(String refreshToken) {
		Long id = jwtProvider.getIdByToken(refreshToken);
		throwIfInvalidToken(refreshToken, id);
		AuthToken authToken = generateTokenPair(id);
		cacheService.put(REFRESH_TOKEN, getCacheKey(REFRESH, id.toString()), authToken.getRefreshToken());
		return authToken;
	}

	private void throwIfInvalidToken(String refreshToken, Long id) {
		String savedToken = cacheService.get(REFRESH_TOKEN, getCacheKey(REFRESH, id.toString()));

		if (savedToken == null || !savedToken.equals(refreshToken)) {
			throw new BaseException(ErrorCode.TOKEN_INVALID);
		}
	}

	private Member createMemberByEmail(String userEmail, Provider provider) {
		Member member = Member.builder()
			.rsoLinked(false)
			.favoriteChampionID(null)
			.status(Status.OFFLINE)
			.upCount((long) INITIAL_COUNT.getValue())
			.build();
		memberRepository.save(member);

		OauthInfo oauthInfo = OauthInfo.builder()
			.provider(provider)
			.email(userEmail)
			.member(member)
			.build();
		oauthInfoRepository.save(oauthInfo);
		return member;
	}

	private String generateTemporaryNickname(Long id) {
		return NICKNAME.getPrefix() + id;
	}

	private String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	private void throwIfMismatchPassword(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BaseException(ErrorCode.INVALID_LOGIN);
		}
	}

	private AuthToken generateTokenPair(Long id) {
		String accessToken = jwtProvider.generateToken(id, ACCESS_TOKEN_EXPIRATION.getExpiration(),
			SUBJECT_ACCESS_TOKEN.getContent());
		String refreshToken = jwtProvider.generateToken(id, REFRESH_TOKEN_EXPIRATION.getExpiration(),
			SUBJECT_REFRESH_TOKEN.getContent());

		return AuthToken.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
