package com.gnimty.communityapiserver.domain.member.service.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordEmailVerifyServiceResponse {

	private String uuid;
}
