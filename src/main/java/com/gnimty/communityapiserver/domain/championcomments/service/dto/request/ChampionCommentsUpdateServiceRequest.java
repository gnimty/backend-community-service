package com.gnimty.communityapiserver.domain.championcomments.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChampionCommentsUpdateServiceRequest {

    private Long mentionedMemberId;
    private String contents;

}
