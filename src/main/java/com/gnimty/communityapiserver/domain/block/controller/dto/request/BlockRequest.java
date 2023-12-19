package com.gnimty.communityapiserver.domain.block.controller.dto.request;

import static com.gnimty.communityapiserver.global.constant.Bound.MAX_MEMO_SIZE;

import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequest {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long id;

	@Size(max = MAX_MEMO_SIZE)
	private String memo;

	public BlockServiceRequest toServiceRequest() {
		return BlockServiceRequest.builder()
			.id(id)
			.memo(memo)
			.build();
	}
}
