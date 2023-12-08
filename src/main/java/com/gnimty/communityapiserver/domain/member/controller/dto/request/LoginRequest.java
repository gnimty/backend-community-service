package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.LoginServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

	private String email;
	private String password;

	public LoginServiceRequest toServiceRequest() {
		return LoginServiceRequest.builder()
			.email(email)
			.password(password)
			.build();
	}
}
