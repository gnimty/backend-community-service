package com.gnimty.communityapiserver.domain.championcomments.controller.dto.request;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsServiceRequest;
import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.validation.annotation.IsChildComments;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
@IsChildComments(parentChampionCommentsId = "parentChampionCommentsId", depth = "depth")
public class ChampionCommentsRequest {

	private Lane lane;
	private Long opponentChampionId;
	@NotNull(message = INVALID_INPUT_VALUE)
	@Min(0)
	@Max(1)
	private Integer depth;
	private Long mentionedMemberId;
	@NotNull
	@Size(max = 1000)
	private String contents;
	@NotNull(message = INVALID_INPUT_VALUE)
	private CommentsType commentsType;
	private Long parentChampionCommentsId;

	public ChampionCommentsServiceRequest toServiceRequest() {
		return ChampionCommentsServiceRequest.builder()
			.lane(lane)
			.opponentChampionId(opponentChampionId)
			.depth(depth)
			.mentionedMemberId(mentionedMemberId)
			.contents(contents)
			.commentsType(commentsType)
			.parentChampionCommentsId(parentChampionCommentsId)
			.build();
	}
}
