package com.gnimty.communityapiserver.global.auth;


import static com.gnimty.communityapiserver.global.constant.Auth.AUTH_TYPE;
import static com.gnimty.communityapiserver.global.constant.Auth.ID_PAYLOAD_NAME;
import static com.gnimty.communityapiserver.global.constant.Auth.JWT_TYPE;
import static com.gnimty.communityapiserver.global.constant.Auth.SUBJECT_ACCESS_TOKEN;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.TOKEN_EXPIRED;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.TOKEN_INVALID;
import static org.springframework.http.HttpHeaders.COOKIE;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.global.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtProvider {

	@Value("${jwt.secret}")
	private String secret;
	private final MemberReadService memberReadService;

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
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return Optional.empty();
		}
		return Arrays.stream(cookies)
			.filter(cookie -> cookie.getName().equals(SUBJECT_ACCESS_TOKEN.getContent()))
			.findFirst()
			.map(Cookie::getValue);
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

	private Claims generateClaims(Long id) {
		Claims claims = Jwts.claims();
		claims.put(ID_PAYLOAD_NAME.getContent(), id);
		return claims;
	}

	private byte[] generateKey() {
		return secret.getBytes(StandardCharsets.UTF_8);
	}

	public String extractJwt(final StompHeaderAccessor accessor) {
		return accessor.getFirstNativeHeader(COOKIE);
	}
}
