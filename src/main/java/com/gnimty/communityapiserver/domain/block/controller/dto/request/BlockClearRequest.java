package com.gnimty.communityapiserver.domain.block.controller.dto.request;

import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockClearServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BlockClearRequest {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long id;

	public BlockClearServiceRequest toServiceRequest() {
		return BlockClearServiceRequest.builder()
			.id(id)
			.build();
	}
}
