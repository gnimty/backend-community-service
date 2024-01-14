package com.gnimty.communityapiserver.domain.championcomments.controller;

import static com.gnimty.communityapiserver.global.constant.ApiSummary.ADD_CHAMPION_COMMENTS;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.DELETE_CHAMPION_COMMENTS;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.READ_CHAMPION_COMMENTS;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.UPDATE_CHAMPION_COMMENTS;
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
import com.gnimty.communityapiserver.global.constant.ApiDescription;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "/champions/{champion_id}/comments", description = "챔피언 운용법 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/champions/{champion_id}/comments")
public class ChampionCommentsController {

	private final ChampionCommentsService championCommentsService;
	private final ChampionCommentsReadService championCommentsReadService;

	@Operation(summary = READ_CHAMPION_COMMENTS, description = ApiDescription.READ_CHAMPION_COMMENTS)
	@GetMapping
	public CommonResponse<ChampionCommentsResponse> readChampionComments(
		@Schema(example = "1", description = "조회하려는 챔피언 id") @PathVariable("champion_id") Long championId
	) {
		ChampionCommentsServiceResponse response = championCommentsReadService.findByChampionId(championId);
		return CommonResponse.success(ChampionCommentsResponse.of(response));
	}

	@Operation(summary = ADD_CHAMPION_COMMENTS, description = ApiDescription.ADD_CHAMPION_COMMENTS)
	@Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
	@PostMapping
	@ResponseStatus(CREATED)
	public CommonResponse<Void> addChampionComments(
		@Schema(example = "1", description = "조회하려는 챔피언 id") @PathVariable("champion_id") Long championId,
		@RequestBody @Valid ChampionCommentsRequest request
	) {
		championCommentsService.addComments(championId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_ADD_CHAMPION_COMMENTS, CREATED);
	}

	@Operation(summary = UPDATE_CHAMPION_COMMENTS, description = ApiDescription.UPDATE_CHAMPION_COMMENTS)
	@Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
	@PatchMapping("/{comments_id}")
	public CommonResponse<Void> updateChampionComments(
		@Schema(example = "1", description = "조회하려는 챔피언 id") @PathVariable("champion_id") Long championId,
		@Schema(example = "1", description = "변경하려는 댓글 id") @PathVariable("comments_id") Long commentsId,
		@RequestBody @Valid ChampionCommentsUpdateRequest request
	) {
		championCommentsService.updateComments(championId, commentsId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_UPDATE_CHAMPION_COMMENTS, OK);
	}

	@Operation(summary = DELETE_CHAMPION_COMMENTS, description = ApiDescription.DELETE_CHAMPION_COMMENTS)
	@Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
	@DeleteMapping("{comments_id}")
	public CommonResponse<Void> deleteChampionComments(
		@Schema(example = "1", description = "조회하려는 챔피언 id") @PathVariable("champion_id") Long championId,
		@Schema(example = "1", description = "삭제하려는 댓글 id") @PathVariable("comments_id") Long commentsId
	) {
		championCommentsService.deleteComments(championId, commentsId);
		return CommonResponse.success(SUCCESS_DELETE_CHAMPION_COMMENTS, OK);
	}
}
