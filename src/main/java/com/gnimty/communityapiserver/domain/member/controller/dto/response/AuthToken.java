package com.gnimty.communityapiserver.domain.member.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthToken {

	@Schema(example = "access token", description = "access token")
	private String accessToken;
	@Schema(example = "refresh token", description = "refresh token")
	private String refreshToken;
}
