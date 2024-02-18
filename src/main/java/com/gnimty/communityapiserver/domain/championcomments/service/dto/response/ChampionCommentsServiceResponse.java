package com.gnimty.communityapiserver.domain.championcomments.service.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChampionCommentsServiceResponse {

    List<ChampionCommentsEntry> championComments;
}
