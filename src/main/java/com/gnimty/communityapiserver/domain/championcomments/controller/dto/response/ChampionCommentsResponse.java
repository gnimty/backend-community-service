package com.gnimty.communityapiserver.domain.championcomments.controller.dto.response;

import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsEntry;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsServiceResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChampionCommentsResponse {

    @Schema(description = "챔피언 운용법 정보")
    List<ChampionCommentsEntry> championComments;

    public static ChampionCommentsResponse of(ChampionCommentsServiceResponse response) {
        return ChampionCommentsResponse.builder()
            .championComments(response.getChampionComments())
            .build();
    }
}
