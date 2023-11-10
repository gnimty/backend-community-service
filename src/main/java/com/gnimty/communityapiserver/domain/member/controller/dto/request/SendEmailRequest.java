package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.SendEmailServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

	private String email;

	public SendEmailServiceRequest toServiceRequest() {
		return SendEmailServiceRequest.builder()
			.email(email)
			.build();
	}
}
