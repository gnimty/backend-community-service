package com.gnimty.communityapiserver.domain.championcommentslike.controller;

import static com.gnimty.communityapiserver.global.constant.ApiSummary.DO_CHAMPION_COMMENTS_LIKE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_CHAMPION_COMMENTS_LIKE;
import static org.springframework.http.HttpStatus.CREATED;

import com.gnimty.communityapiserver.domain.championcommentslike.controller.dto.request.ChampionCommentsLikeRequest;
import com.gnimty.communityapiserver.domain.championcommentslike.service.ChampionCommentsLikeService;
import com.gnimty.communityapiserver.global.constant.ApiDescription;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/champions/{champion_id}/comments/{comments_id}/like", description = "챔피언 운용법 좋아요 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/champions/{champion_id}/comments/{comments_id}/like")
public class ChampionCommentsLikeController {

    private final ChampionCommentsLikeService championCommentsLikeService;

    @Operation(summary = DO_CHAMPION_COMMENTS_LIKE, description = ApiDescription.DO_CHAMPION_COMMENTS_LIKE)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @PostMapping
    public CommonResponse<Void> doChampionCommentsLike(
        @Schema(example = "1", description = "조회하려는 챔피언 id") @PathVariable("champion_id") Long championId,
        @Schema(example = "1", description = "좋아요하려는 댓글 id") @PathVariable("comments_id") Long commentsId,
        @RequestBody @Valid ChampionCommentsLikeRequest request
    ) {
        championCommentsLikeService.doChampionCommentsLike(championId, commentsId, request.toServiceRequest());
        return CommonResponse.success(SUCCESS_CHAMPION_COMMENTS_LIKE, CREATED);
    }
}
