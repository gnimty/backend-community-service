package com.gnimty.communityapiserver.global.interceptor;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.Auth;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Component
public class TokenAuthInterceptor implements HandlerInterceptor {

	private final JwtProvider jwtProvider;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String path = request.getServletPath();
		String method = request.getMethod();
		if (skipTokenCheckBeforeHeader(path, method)) {
			return true;
		}
		Optional<String> tokenByHeader = jwtProvider.resolveToken(request);
		if (tokenByHeader.isEmpty()) {
			if (skipTokenCheckAfterHeader(path, method)) {
				return true;
			}
			throw new BaseException(ErrorCode.TOKEN_NOT_FOUND);
		}

		String token = tokenByHeader.get().replaceFirst(Auth.BEARER.getContent(), "");
		jwtProvider.checkValidation(token);
		Member member = jwtProvider.findMemberByToken(token);
		MemberThreadLocal.set(member);

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
		ModelAndView modelAndView) throws Exception {
		Member member = MemberThreadLocal.get();

		if (member == null) {
			return;
		}

		MemberThreadLocal.remove();
	}

	private boolean skipTokenCheckBeforeHeader(String path, String method) {
		if (path.contains("/members") && Character.isDigit(lastPathSegment(path)) && HttpMethod.GET.matches(method)) {
			return true;
		}
		return path.equals("/summoners") && HttpMethod.PATCH.matches(method);
	}

	private boolean skipTokenCheckAfterHeader(String path, String method) {
		return (path.contains("/champions") || path.equals("/summoners/main")) && HttpMethod.GET.matches(method);
	}

	private char lastPathSegment(String path) {
		String[] segments = path.split("/");
		return segments[segments.length - 1].charAt(0);
	}
}
