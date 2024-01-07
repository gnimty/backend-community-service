package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordUpdateServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
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
public class PasswordUpdateRequest {

	@Schema(example = "Abc1234***", description = "현재 비밀번호, not null, password pattern")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	@Pattern(regexp = RequestPattern.PASSWORD_PATTERN, message = ErrorMessage.INVALID_INPUT_VALUE)
	private String currentPassword;
	@Schema(example = "Abc12345**", description = "변경할 비밀번호, not null, password pattern")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	@Pattern(regexp = RequestPattern.PASSWORD_PATTERN, message = ErrorMessage.INVALID_INPUT_VALUE)
	private String newPassword;

	public PasswordUpdateServiceRequest toServiceRequest() {
		return PasswordUpdateServiceRequest.builder()
			.currentPassword(currentPassword)
			.newPassword(newPassword)
			.build();
	}
}
