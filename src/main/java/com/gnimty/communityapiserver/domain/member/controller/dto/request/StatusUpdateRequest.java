package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.StatusUpdateServiceRequest;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class StatusUpdateRequest {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Status status;

	public StatusUpdateServiceRequest toServiceRequest() {
		return StatusUpdateServiceRequest.builder()
			.status(status)
			.build();
	}
}
