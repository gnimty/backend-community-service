package com.gnimty.communityapiserver.domain.championcomments.controller;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_ADD_CHAMPION_COMMENTS;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_DELETE_CHAMPION_COMMENTS;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_CHAMPION_COMMENTS;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.championcomments.controller.dto.request.ChampionCommentsRequest;
import com.gnimty.communityapiserver.domain.championcomments.controller.dto.request.ChampionCommentsUpdateRequest;
import com.gnimty.communityapiserver.domain.championcomments.controller.dto.response.ChampionCommentsResponse;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsReadService;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsService;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsServiceResponse;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/champions/{champion_id}/comments")
public class ChampionCommentsController {

	private final ChampionCommentsService championCommentsService;
	private final ChampionCommentsReadService championCommentsReadService;

	@GetMapping
	public CommonResponse<ChampionCommentsResponse> readChampionComments(
		@PathVariable("champion_id") Long championId
	) {
		ChampionCommentsServiceResponse response = championCommentsReadService
			.findByChampionId(championId);
		return CommonResponse.success(ChampionCommentsResponse.of(response));
	}

	@PostMapping
	@ResponseStatus(CREATED)
	public CommonResponse<Void> addChampionComments(
		@PathVariable("champion_id") Long championId,
		@RequestBody @Valid ChampionCommentsRequest request
	) {
		championCommentsService.addComments(championId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_ADD_CHAMPION_COMMENTS, CREATED);
	}

	@PatchMapping("/{comments_id}")
	public CommonResponse<Void> updateChampionComments(
		@PathVariable("champion_id") Long championId,
		@PathVariable("comments_id") Long commentsId,
		@RequestBody @Valid ChampionCommentsUpdateRequest request
	) {
		championCommentsService.updateComments(championId, commentsId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_UPDATE_CHAMPION_COMMENTS, OK);
	}

	@DeleteMapping("{comments_id}")
	public CommonResponse<Void> deleteChampionComments(
		@PathVariable("champion_id") Long championId,
		@PathVariable("comments_id") Long commentsId
	) {
		championCommentsService.deleteComments(championId, commentsId);
		return CommonResponse.success(SUCCESS_DELETE_CHAMPION_COMMENTS, OK);
	}
}
