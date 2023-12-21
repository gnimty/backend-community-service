package com.gnimty.communityapiserver.domain.championcommentsreport.service.dto.request;

import com.gnimty.communityapiserver.global.constant.ReportType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChampionCommentsReportServiceRequest {

	private ReportType reportType;
	private String reportComment;
}
