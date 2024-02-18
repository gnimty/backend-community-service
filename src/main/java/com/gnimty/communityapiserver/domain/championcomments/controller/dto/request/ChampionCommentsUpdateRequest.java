package com.gnimty.communityapiserver.domain.championcomments.controller.dto.request;

import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsUpdateServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.gnimty.communityapiserver.global.constant.Bound.MAX_CONTENTS_SIZE;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionCommentsUpdateRequest {

    @Schema(example = "1", description = "언급하려는 회원 id")
    private Long mentionedMemberId;
    @Schema(example = "content", description = "댓글 내용, not null, 최대 1000자")
    @NotNull
    @Size(max = MAX_CONTENTS_SIZE)
    private String contents;

    public ChampionCommentsUpdateServiceRequest toServiceRequest() {
        return ChampionCommentsUpdateServiceRequest.builder()
            .mentionedMemberId(mentionedMemberId)
            .contents(contents)
            .build();
    }
}
