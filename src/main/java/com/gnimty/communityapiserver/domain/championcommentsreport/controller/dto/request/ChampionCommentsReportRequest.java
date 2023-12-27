package com.gnimty.communityapiserver.domain.championcommentsreport.controller.dto.request;

import static com.gnimty.communityapiserver.global.constant.Bound.MAX_REPORT_COMMENT_SIZE;

import com.gnimty.communityapiserver.domain.championcommentsreport.service.dto.request.ChampionCommentsReportServiceRequest;
import com.gnimty.communityapiserver.global.constant.ReportType;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
	private List<ReportType> reportType;

	@Size(max = MAX_REPORT_COMMENT_SIZE)
	private String reportComment;

	public ChampionCommentsReportServiceRequest toServiceRequest() {
		return ChampionCommentsReportServiceRequest.builder()
			.reportType(reportType)
			.reportComment(reportComment)
			.build();
	}
}
