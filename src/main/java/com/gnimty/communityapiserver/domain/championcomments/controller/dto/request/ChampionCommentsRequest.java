package com.gnimty.communityapiserver.domain.championcomments.controller.dto.request;

import static com.gnimty.communityapiserver.global.constant.Bound.MAX_CONTENTS_SIZE;
import static com.gnimty.communityapiserver.global.constant.Bound.MAX_DEPTH_SIZE;
import static com.gnimty.communityapiserver.global.constant.Bound.MIN_DEPTH_SIZE;
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
	@Min(MIN_DEPTH_SIZE)
	@Max(MAX_DEPTH_SIZE)
	private Integer depth;
	private Long mentionedMemberId;
	@NotNull
	@Size(max = MAX_CONTENTS_SIZE)
	private String contents;
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
