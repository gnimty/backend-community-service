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
		if (request.getServletPath().equals("/summoners") && request.getMethod().equals(HttpMethod.PATCH.toString())) {
			return true;
		}
		Optional<String> tokenByHeader = jwtProvider.resolveToken(request);
		if (tokenByHeader.isEmpty()) {
			if (request.getServletPath().contains("champions")
				&& request.getMethod().equals(HttpMethod.GET.toString())) {
				return true;
			}
			if (request.getServletPath().equals("/summoners/main")
				&& request.getMethod().equals(HttpMethod.GET.toString())) {
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
}
