package com.gnimty.communityapiserver.domain.championcomments.controller.dto.request;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsUpdateServiceRequest;
import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChampionCommentsUpdateRequest {

    private Lane lane;
    private Long opponentChampionId;
    private Long mentionedMemberId;
    @NotNull
    @Size(max = 1000)
    private String contents;
    @NotNull(message = INVALID_INPUT_VALUE)
    private CommentsType commentsType;

    public ChampionCommentsUpdateServiceRequest toServiceRequest() {
        return ChampionCommentsUpdateServiceRequest.builder()
            .lane(lane)
            .opponentChampionId(opponentChampionId)
            .mentionedMemberId(mentionedMemberId)
            .contents(contents)
            .commentsType(commentsType)
            .build();
    }
}
