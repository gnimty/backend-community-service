package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.OauthLoginServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OauthLoginRequest {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String authCode;

	public OauthLoginServiceRequest toServiceRequest() {
		return OauthLoginServiceRequest.builder()
			.authCode(authCode)
			.build();
	}
}
