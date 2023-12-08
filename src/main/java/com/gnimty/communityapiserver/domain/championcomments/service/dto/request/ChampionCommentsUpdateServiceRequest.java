package com.gnimty.communityapiserver.domain.championcomments.service.dto.request;

import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChampionCommentsUpdateServiceRequest {

    private Lane lane;
    private Long opponentChampionId;
    private Long mentionedMemberId;
    private String contents;
    private CommentsType commentsType;

}
