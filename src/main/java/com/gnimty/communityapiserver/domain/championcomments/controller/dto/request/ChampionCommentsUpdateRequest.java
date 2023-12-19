package com.gnimty.communityapiserver.domain.championcomments.controller.dto.request;

import static com.gnimty.communityapiserver.global.constant.Bound.MAX_CONTENTS_SIZE;

import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsUpdateServiceRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChampionCommentsUpdateRequest {

	private Long mentionedMemberId;
	@NotNull
	@Size(max = MAX_CONTENTS_SIZE)
	private String contents;

	public ChampionCommentsUpdateServiceRequest toServiceRequest() {
		return ChampionCommentsUpdateServiceRequest.builder()
			.mentionedMemberId(mentionedMemberId)
			.contents(contents)
			.build();
	}
}
