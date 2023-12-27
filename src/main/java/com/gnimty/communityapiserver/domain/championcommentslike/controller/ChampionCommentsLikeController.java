package com.gnimty.communityapiserver.domain.championcommentslike.controller;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_CHAMPION_COMMENTS_LIKE;
import static org.springframework.http.HttpStatus.CREATED;

import com.gnimty.communityapiserver.domain.championcommentslike.controller.dto.request.ChampionCommentsLikeRequest;
import com.gnimty.communityapiserver.domain.championcommentslike.service.ChampionCommentsLikeService;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/champions/{champion_id}/comments/{comments_id}/like")
public class ChampionCommentsLikeController {

	private final ChampionCommentsLikeService championCommentsLikeService;

	@PostMapping
	public CommonResponse<Void> doChampionCommentsLike(
		@PathVariable("champion_id") Long championId,
		@PathVariable("comments_id") Long commentsId,
		@RequestBody @Valid ChampionCommentsLikeRequest request
	) {
		championCommentsLikeService.doChampionCommentsLike(championId, commentsId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_CHAMPION_COMMENTS_LIKE, CREATED);
	}
}
