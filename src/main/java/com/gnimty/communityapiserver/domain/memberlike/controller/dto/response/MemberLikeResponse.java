package com.gnimty.communityapiserver.domain.memberlike.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberLikeResponse {

	@Schema(example = "100", description = "up count")
	private Long upCount;
}
