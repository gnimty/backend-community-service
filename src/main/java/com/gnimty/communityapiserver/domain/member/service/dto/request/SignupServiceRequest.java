package com.gnimty.communityapiserver.domain.member.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupServiceRequest {

	private String email;
	private String password;
	private Boolean agreeTerms;
}
