package com.gnimty.communityapiserver.domain.member.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordResetServiceRequest {

	private String email;
	private String password;
	private String uuid;
}
