package com.gnimty.communityapiserver.domain.championcommentsreport.controller;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_COMMENTS_REPORT;
import static org.springframework.http.HttpStatus.CREATED;

import com.gnimty.communityapiserver.domain.championcommentsreport.controller.dto.request.ChampionCommentsReportRequest;
import com.gnimty.communityapiserver.domain.championcommentsreport.service.ChampionCommentsReportService;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/champions/{champion_id}/comments/{comments_id}/reports")
public class ChampionCommentsReportController {

	private final ChampionCommentsReportService championCommentsReportService;

	@ResponseStatus(CREATED)
	@PostMapping
	public CommonResponse<Void> doReport(
		@PathVariable("champion_id") Long championId,
		@PathVariable("comments_id") Long commentsId,
		@Valid @RequestBody ChampionCommentsReportRequest request
	) {
		championCommentsReportService.doReport(championId, commentsId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_COMMENTS_REPORT, CREATED);
	}
}
