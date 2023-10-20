package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.AGREE_TERMS_MUST_BE_TRUE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

import com.gnimty.communityapiserver.domain.member.service.dto.request.SignupServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {

	@NotNull(message = INVALID_INPUT_VALUE)
	@Pattern(regexp = RequestPattern.EMAIL_PATTERN, message = INVALID_INPUT_VALUE)
	private String email;

	@NotNull(message = INVALID_INPUT_VALUE)
	@Pattern(regexp = RequestPattern.PASSWORD_PATTERN, message = INVALID_INPUT_VALUE)
	private String password;

	@NotNull(message = INVALID_INPUT_VALUE)
	@AssertTrue(message = AGREE_TERMS_MUST_BE_TRUE)
	private Boolean agreeTerms;

	public SignupServiceRequest toServiceRequest() {
		return SignupServiceRequest.builder()
			.email(email)
			.password(password)
			.agreeTerms(agreeTerms)
			.build();
	}
}
