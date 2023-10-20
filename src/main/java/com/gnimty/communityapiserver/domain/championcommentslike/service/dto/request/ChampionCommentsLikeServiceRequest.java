package com.gnimty.communityapiserver.domain.championcommentslike.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChampionCommentsLikeServiceRequest {

	private Boolean likeOrNot;
}
