package com.gnimty.communityapiserver.domain.member.service.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyProfileServiceResponse {

	private Long id;
	private String email;
	private String nickname;
	private Long favoriteChampionId;
	private Long upCount;
	private RiotDependentInfo riotDependentInfo;
	private List<OauthInfoEntry> oauthInfos;
}
