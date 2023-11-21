package com.gnimty.communityapiserver.domain.member.service;

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
import com.gnimty.communityapiserver.global.constant.Auth;
import com.gnimty.communityapiserver.global.constant.KeyPrefix;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.utils.RandomCodeGenerator;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final MemberRepository memberRepository;
	private final OauthInfoRepository oauthInfoRepository;
	private final MemberReadService memberReadService;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;
	private final StringRedisTemplate redisTemplate;
	private final KakaoOauthUtil kakaoOauthUtil;
	private final GoogleOauthUtil googleOauthUtil;
	private final MailSenderUtil mailSenderUtil;

	public void signup(SignupServiceRequest request) {
		memberReadService.throwIfExistByEmail(request.getEmail());
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		String verify = valueOperations.get(getRedisKey(KeyPrefix.SIGNUP, request.getEmail()));
		if (verify == null) {
			throw new BaseException(ErrorCode.UNAUTHORIZED_EMAIL);
		}

		Member member = Member.builder()
			.rsoLinked(false)
			.email(request.getEmail())
			.password(encodePassword(request.getPassword()))
			.favoriteChampionID(null)
			.status(Status.OFFLINE)
			.upCount(0L)
			.build();

		memberRepository.save(member);
		member.updateNickname(generateTemporaryNickname(member.getId()));

		redisTemplate.delete(getRedisKey(KeyPrefix.SIGNUP, request.getEmail()));
	}

	public AuthToken login(LoginServiceRequest request) {
		Member member = memberReadService.findByEmailOrElseThrow(request.getEmail(),
			new BaseException(ErrorCode.INVALID_LOGIN));
		throwIfMismatchPassword(request.getPassword(), member.getPassword());

		AuthToken authToken = generateTokenPair(member.getId());
		saveInRedis(
			getRedisKey(KeyPrefix.REFRESH, String.valueOf(member.getId())),
			authToken.getRefreshToken().replaceAll(Auth.BEARER.getContent(), ""),
			Auth.REFRESH_TOKEN_EXPIRATION.getExpiration());
		return authToken;
	}

	public AuthToken kakaoLogin(OauthLoginServiceRequest request) {
		String userEmail = kakaoOauthUtil.getKakaoUserEmail(request.getAuthCode());

		Member member = oauthInfoRepository.findByEmail(userEmail)
			.map(OauthInfo::getMember)
			.orElseGet(() -> createMemberByEmail(userEmail, Provider.KAKAO));
		AuthToken authToken = generateTokenPair(member.getId());
		saveInRedis(
			getRedisKey(KeyPrefix.REFRESH, String.valueOf(member.getId())),
			authToken.getRefreshToken().replaceAll(Auth.BEARER.getContent(), ""),
			Auth.REFRESH_TOKEN_EXPIRATION.getExpiration());
		return authToken;
	}

	public AuthToken googleLogin(OauthLoginServiceRequest request) {
		String googleUserEmail = googleOauthUtil.getGoogleUserEmail(request.getAuthCode());

		Member member = oauthInfoRepository.findByEmail(googleUserEmail)
			.map(OauthInfo::getMember)
			.orElseGet(() -> createMemberByEmail(googleUserEmail, Provider.GOOGLE));
		AuthToken authToken = generateTokenPair(member.getId());
		saveInRedis(
			getRedisKey(KeyPrefix.REFRESH, String.valueOf(member.getId())),
			authToken.getRefreshToken().replaceAll(Auth.BEARER.getContent(), ""),
			Auth.REFRESH_TOKEN_EXPIRATION.getExpiration());
		return authToken;
	}

	@Async("mailExecutor")
	public void sendEmailAuthCode(EmailAuthServiceRequest request) {

		String code = RandomCodeGenerator.generateCodeByLength(6);
		mailSenderUtil.sendEmail(Auth.EMAIL_SUBJECT.getContent(), request.getEmail(), code,
			"signup-mail", "static/images/banner-pengu.png");
		String key = getRedisKey(KeyPrefix.EMAIL, request.getEmail());
		saveInRedis(key, code, Auth.EMAIL_CODE_EXPIRATION.getExpiration());
	}

	public void verifyEmailAuthCode(EmailVerifyServiceRequest request) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		String emailAuthKey = getRedisKey(KeyPrefix.EMAIL, request.getEmail());
		String signupKey = getRedisKey(KeyPrefix.SIGNUP, request.getEmail());
		String savedCode = valueOperations.get(emailAuthKey);

		if (!request.getCode().equals(savedCode)) {
			throw new BaseException(ErrorCode.INVALID_EMAIL_AUTH_CODE);
		}

		saveInRedis(signupKey, "verified", Auth.SIGNUP_EXPIRATION.getExpiration());
		redisTemplate.delete(emailAuthKey);
	}

	public AuthToken tokenRefresh(String refreshToken) {
		Long idByToken = jwtProvider.getIdByToken(refreshToken);
		throwIfInvalidToken(refreshToken, idByToken);
		AuthToken authToken = generateTokenPair(idByToken);
		saveInRedis(
			getRedisKey(KeyPrefix.REFRESH, String.valueOf(idByToken)),
			authToken.getRefreshToken(),
			Auth.REFRESH_TOKEN_EXPIRATION.getExpiration()
		);
		return authToken;
	}

	private void throwIfInvalidToken(String refreshToken, Long idByToken) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
		String savedToken = valueOperations.get(
			getRedisKey(KeyPrefix.REFRESH, String.valueOf(idByToken)));

		if (savedToken == null || !savedToken.equals(refreshToken)) {
			throw new BaseException(ErrorCode.TOKEN_INVALID);
		}
	}

	private Member createMemberByEmail(String userEmail, Provider provider) {
		Member member = Member.builder()
			.rsoLinked(false)
			.favoriteChampionID(null)
			.status(Status.OFFLINE)
			.upCount(0L)
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
		return KeyPrefix.NICKNAME.getPrefix() + id;
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
		String accessToken = Auth.BEARER.getContent() + jwtProvider.generateToken(id,
			Auth.ACCESS_TOKEN_EXPIRATION.getExpiration(), Auth.SUBJECT_ACCESS_TOKEN.getContent());
		String refreshToken = Auth.BEARER.getContent() + jwtProvider.generateToken(id,
			Auth.REFRESH_TOKEN_EXPIRATION.getExpiration(), Auth.SUBJECT_REFRESH_TOKEN.getContent());

		return AuthToken.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	private void saveInRedis(String key, String value, long timeout) {
		ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();

		stringValueOperations.set(key, value);
		redisTemplate.expire(
			key,
			timeout,
			TimeUnit.MILLISECONDS
		);
	}

	private String getRedisKey(KeyPrefix prefix, String key) {
		return prefix.getPrefix() + key;
	}
}
