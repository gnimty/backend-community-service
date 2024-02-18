package com.gnimty.communityapiserver.domain.championcommentslike.controller.dto.request;

import com.gnimty.communityapiserver.domain.championcommentslike.service.dto.request.ChampionCommentsLikeServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionCommentsLikeRequest {

	@Schema(example = "true", description = "좋아요 또는 싫어요 여부, not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Boolean likeOrNot;
	@Schema(example = "true", description = "취소 여부, not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Boolean cancel;

	public ChampionCommentsLikeServiceRequest toServiceRequest() {
		return ChampionCommentsLikeServiceRequest.builder()
			.likeOrNot(likeOrNot)
			.cancel(cancel)
			.build();
	}
}
