package com.gnimty.communityapiserver.domain.member.controller.dto.response;

import com.gnimty.communityapiserver.domain.member.service.dto.response.PasswordEmailVerifyServiceResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordEmailVerifyResponse {

	@Schema(example = "alksdfkljalsjd-flskdnflk", description = "uuid")
	private String uuid;

	public static PasswordEmailVerifyResponse from(PasswordEmailVerifyServiceResponse response) {
		return PasswordEmailVerifyResponse.builder()
			.uuid(response.getUuid())
			.build();
	}
}
