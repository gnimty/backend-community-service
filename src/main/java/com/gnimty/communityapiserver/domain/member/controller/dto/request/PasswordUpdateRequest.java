package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordUpdateServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	@Pattern(regexp = RequestPattern.PASSWORD_PATTERN, message = ErrorMessage.INVALID_INPUT_VALUE)
	private String password;

	public PasswordUpdateServiceRequest toServiceRequest() {
		return PasswordUpdateServiceRequest.builder()
			.password(password)
			.build();
	}
}
