package com.gnimty.communityapiserver.domain.championcomments.service.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChampionCommentsServiceResponse {

    List<ChampionCommentsEntry> championComments;
}
