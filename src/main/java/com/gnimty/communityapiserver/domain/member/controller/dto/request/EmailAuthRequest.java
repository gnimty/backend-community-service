package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

import com.gnimty.communityapiserver.domain.member.service.dto.request.EmailAuthServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAuthRequest {

	@Schema(example = "email@email.com", description = "이메일, not null, email pattern")
	@NotNull(message = INVALID_INPUT_VALUE)
	@Pattern(regexp = RequestPattern.EMAIL_PATTERN, message = INVALID_INPUT_VALUE)
	private String email;

	public EmailAuthServiceRequest toServiceRequest() {
		return EmailAuthServiceRequest.builder()
			.email(email)
			.build();
	}
}
