package com.gnimty.communityapiserver.domain.championcomments.controller.dto.response;

import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsEntry;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsServiceResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChampionCommentsResponse {

	List<ChampionCommentsEntry> championComments;

	public static ChampionCommentsResponse of(ChampionCommentsServiceResponse response) {
		return ChampionCommentsResponse.builder()
			.championComments(response.getChampionComments())
			.build();
	}
}
