package com.gnimty.communityapiserver.global.interceptor;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class MemberAuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
		Object handler) throws Exception {

		Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(
			HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Member member = MemberThreadLocal.get();

		String memberIdStr = pathVariables.get("member_id");

		try {
			Long memberId = Long.valueOf(memberIdStr);
			if (!Objects.equals(member.getId(), memberId)) {
				throwNoPermissionException();
			}
		} catch (NumberFormatException e) {
			throw new BaseException(ErrorCode.INVALID_PATH_VARIABLE);
		}

		return true;
	}

	public void throwNoPermissionException() {
		throw new BaseException(ErrorCode.NO_PERMISSION);
	}
}
