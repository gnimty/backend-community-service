package com.gnimty.communityapiserver.domain.championcommentsreport.controller;

import static com.gnimty.communityapiserver.global.constant.ApiSummary.DO_REPORT;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_COMMENTS_REPORT;
import static org.springframework.http.HttpStatus.CREATED;

import com.gnimty.communityapiserver.domain.championcommentsreport.controller.dto.request.ChampionCommentsReportRequest;
import com.gnimty.communityapiserver.domain.championcommentsreport.service.ChampionCommentsReportService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/champions/{champion_id}/comments/{comments_id}/reports", description = "챔피언 운용법 신고 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/champions/{champion_id}/comments/{comments_id}/reports")
public class ChampionCommentsReportController {

	private final ChampionCommentsReportService championCommentsReportService;

	@Operation(summary = DO_REPORT, description = ApiDescription.DO_REPORT)
	@Parameter(in = ParameterIn.COOKIE, name = "accessToken", description = "인증을 위한 Access Token", required = true)
	@ResponseStatus(CREATED)
	@PostMapping
	public CommonResponse<Void> doReport(
		@Schema(example = "1", description = "조회하려는 챔피언 id") @PathVariable("champion_id") Long championId,
		@Schema(example = "1", description = "신고하려는 댓글 id") @PathVariable("comments_id") Long commentsId,
		@Valid @RequestBody ChampionCommentsReportRequest request
	) {
		championCommentsReportService.doReport(championId, commentsId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_COMMENTS_REPORT, CREATED);
	}
}
