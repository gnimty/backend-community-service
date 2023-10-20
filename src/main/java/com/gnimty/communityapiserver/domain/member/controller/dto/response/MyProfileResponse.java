package com.gnimty.communityapiserver.domain.member.controller.dto.response;

import com.gnimty.communityapiserver.domain.member.service.dto.response.MyProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OauthInfoEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.RiotDependentInfo;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyProfileResponse {

	private Long id;
	private String email;
	private String nickname;
	private Long favoriteChampionId;
	private Long upCount;
	private RiotDependentInfo riotDependentInfo;
	private List<OauthInfoEntry> oauthInfos;

	public static MyProfileResponse from(MyProfileServiceResponse response) {
		return MyProfileResponse.builder()
			.id(response.getId())
			.email(response.getEmail())
			.nickname(response.getNickname())
			.favoriteChampionId(response.getFavoriteChampionId())
			.upCount(response.getUpCount())
			.riotDependentInfo(response.getRiotDependentInfo())
			.oauthInfos(response.getOauthInfos())
			.build();
	}
}