package com.gnimty.communityapiserver.domain.member.controller.dto.response;

import com.gnimty.communityapiserver.domain.member.service.dto.response.PasswordEmailVerifyServiceResponse;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordEmailVerifyResponse {

	private String uuid;

	public static PasswordEmailVerifyResponse from(PasswordEmailVerifyServiceResponse response) {
		return PasswordEmailVerifyResponse.builder()
			.uuid(response.getUuid())
			.build();
	}
}
