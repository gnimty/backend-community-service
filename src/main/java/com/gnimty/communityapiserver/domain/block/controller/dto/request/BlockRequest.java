package com.gnimty.communityapiserver.domain.block.controller.dto.request;

import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class BlockRequest {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long id;

	@Size(max = 100)
	private String memo;

	public BlockServiceRequest toServiceRequest() {
		return BlockServiceRequest.builder()
			.id(id)
			.memo(memo)
			.build();
	}
}
