package com.gnimty.communityapiserver.domain.championcomments.controller.dto.request;

import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsServiceRequest;
import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.validation.annotation.IsChildComments;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.gnimty.communityapiserver.global.constant.Bound.*;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IsChildComments(parentChampionCommentsId = "parentChampionCommentsId", depth = "depth")
public class ChampionCommentsRequest {

    @Schema(example = "TOP", description = "라인")
    private Lane lane;
    @Schema(example = "1", description = "상대 챔피언 id")
    private Long opponentChampionId;
    @Schema(example = "0", description = "댓글 깊이, not null, 최소 0, 최대 1")
    @NotNull(message = INVALID_INPUT_VALUE)
    @Min(MIN_DEPTH_SIZE)
    @Max(MAX_DEPTH_SIZE)
    private Integer depth;
    @Schema(example = "1", description = "언급하려는 회원 id")
    private Long mentionedMemberId;
    @Schema(example = "content", description = "댓글 내용, not null, 최대 1000자")
    @NotNull
    @Size(max = MAX_CONTENTS_SIZE)
    private String contents;
    @Schema(example = "TIP", description = "댓글 타입")
    private CommentsType commentsType;
    @Schema(example = "1", description = "자식 댓글인 경우, 부모 댓글의 id")
    private Long parentChampionCommentsId;

    public ChampionCommentsServiceRequest toServiceRequest() {
        return ChampionCommentsServiceRequest.builder()
            .lane(lane)
            .opponentChampionId(opponentChampionId)
            .depth(depth)
            .mentionedMemberId(mentionedMemberId)
            .contents(contents)
            .commentsType(commentsType)
            .parentChampionCommentsId(parentChampionCommentsId)
            .build();
    }
}
