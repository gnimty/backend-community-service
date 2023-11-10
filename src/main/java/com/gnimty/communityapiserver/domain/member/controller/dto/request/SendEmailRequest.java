package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.SendEmailServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String email;

	public SendEmailServiceRequest toServiceRequest() {
		return SendEmailServiceRequest.builder()
			.email(email)
			.build();
	}
}
