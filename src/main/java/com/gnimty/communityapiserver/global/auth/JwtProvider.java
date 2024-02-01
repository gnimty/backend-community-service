package com.gnimty.communityapiserver.global.auth;


import static com.gnimty.communityapiserver.global.constant.Auth.ACCESS_TOKEN_EXPIRATION;
import static com.gnimty.communityapiserver.global.constant.Auth.AUTHORIZATION;
import static com.gnimty.communityapiserver.global.constant.Auth.AUTH_TYPE;
import static com.gnimty.communityapiserver.global.constant.Auth.BEARER;
import static com.gnimty.communityapiserver.global.constant.Auth.EMAIL_PAYLOAD_NAME;
import static com.gnimty.communityapiserver.global.constant.Auth.ID_PAYLOAD_NAME;
import static com.gnimty.communityapiserver.global.constant.Auth.JWT_TYPE;
import static com.gnimty.communityapiserver.global.constant.Auth.REFRESH_TOKEN_EXPIRATION;
import static com.gnimty.communityapiserver.global.constant.Auth.SUBJECT_ACCESS_TOKEN;
import static com.gnimty.communityapiserver.global.constant.Auth.SUBJECT_REFRESH_TOKEN;
import static com.gnimty.communityapiserver.global.constant.Auth.TOKEN_SPLITTER;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.TOKEN_EXPIRED;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.TOKEN_INVALID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnimty.communityapiserver.domain.member.controller.dto.response.AuthToken;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.global.constant.KeyPrefix;
import com.gnimty.communityapiserver.global.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	private String secret;
	private final MemberReadService memberReadService;
	private final StringRedisTemplate redisTemplate;

	public Member findMemberByToken(String token) {
		return memberReadService.findById(getIdByToken(token));
	}

	public String generateToken(Long id, Long expiration, String subject) {
		Date issueDate = new Date();
		Date expireDate = new Date();
		expireDate.setTime(issueDate.getTime() + expiration);
		return Jwts.builder()
			.setHeaderParam(AUTH_TYPE.getContent(), JWT_TYPE.getContent())
			.setClaims(generateClaims(id))
			.setIssuedAt(issueDate)
			.setSubject(subject)
			.setExpiration(expireDate)
			.signWith(SignatureAlgorithm.HS256, generateKey())
			.compact();
	}

	public AuthToken generateTokenByRefreshToken(String refreshToken) {
		Member member = findMemberByToken(refreshToken);

		if (!checkRefreshTokenEquals(member, refreshToken)) {
			throw new BaseException(TOKEN_INVALID);
		}

		String newAccessToken = BEARER.getContent() + generateToken(
			member.getId(),
			ACCESS_TOKEN_EXPIRATION.getExpiration(),
			SUBJECT_ACCESS_TOKEN.getContent()
		);

		String newRefreshToken = BEARER.getContent() + generateToken(
			member.getId(),
			REFRESH_TOKEN_EXPIRATION.getExpiration(),
			SUBJECT_REFRESH_TOKEN.getContent()
		);

		return AuthToken.builder()
			.accessToken(newAccessToken)
			.refreshToken(newRefreshToken)
			.build();
	}

	public void checkValidation(String token) {
		try {
			Jwts.parser()
				.setSigningKey(generateKey())
				.parseClaimsJws(token);
		} catch (ExpiredJwtException e) {
			throw new BaseException(TOKEN_EXPIRED);
		} catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
			throw new BaseException(TOKEN_INVALID);
		}
	}

	public Optional<String> resolveToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(AUTHORIZATION.getContent()));
	}

	public Long getIdByToken(String token) {
		try {
			return Long.parseLong(String.valueOf(Jwts.parser()
				.setSigningKey(generateKey())
				.parseClaimsJws(token)
				.getBody()
				.get(ID_PAYLOAD_NAME.getContent())));
		} catch (ExpiredJwtException e) {
			throw new BaseException(TOKEN_EXPIRED);
		} catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
			throw new BaseException(TOKEN_INVALID);
		}
	}

	private boolean checkRefreshTokenEquals(Member member, String refreshToken) {
		ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();

		String key = getRefreshKey(member);
		String value = stringValueOperations.get(key);

		if (value == null || !value.equals(refreshToken)) {
			redisTemplate.delete(key);
			return false;
		}

		stringValueOperations.set(key, value);
		return true;
	}

	private String getRefreshKey(Member member) {
		return KeyPrefix.REFRESH.getPrefix() + member.getId();
	}

	private Claims generateClaims(Long id) {
		Claims claims = Jwts.claims();
		claims.put(ID_PAYLOAD_NAME.getContent(), id);
		return claims;
	}

	private byte[] generateKey() {
		return secret.getBytes(StandardCharsets.UTF_8);
	}

	public String extractJwt(final StompHeaderAccessor accessor) {
		return accessor.getFirstNativeHeader(AUTHORIZATION.getContent());
	}
}
