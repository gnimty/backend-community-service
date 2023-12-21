package com.gnimty.communityapiserver.domain.championcommentsreport.controller.dto.request;

import com.gnimty.communityapiserver.domain.championcommentsreport.service.dto.request.ChampionCommentsReportServiceRequest;
import com.gnimty.communityapiserver.global.constant.ReportType;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChampionCommentsReportRequest {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private ReportType reportType;
	private String reportComment;

	public ChampionCommentsReportServiceRequest toServiceRequest() {
		return ChampionCommentsReportServiceRequest.builder()
			.reportType(reportType)
			.reportComment(reportComment)
			.build();
	}
}
