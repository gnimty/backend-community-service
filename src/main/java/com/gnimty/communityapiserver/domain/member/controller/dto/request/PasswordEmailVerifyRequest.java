package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordEmailVerifyServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PasswordEmailVerifyRequest {

	@NotNull(message = INVALID_INPUT_VALUE)
	@Pattern(regexp = RequestPattern.EMAIL_AUTH_CODE_PATTERN, message = INVALID_INPUT_VALUE)
	private String code;

	public PasswordEmailVerifyServiceRequest toServiceRequest() {
		return PasswordEmailVerifyServiceRequest.builder()
			.code(code)
			.build();
	}
}
