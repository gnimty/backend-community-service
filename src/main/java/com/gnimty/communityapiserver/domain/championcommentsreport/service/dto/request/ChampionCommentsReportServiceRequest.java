package com.gnimty.communityapiserver.domain.championcommentsreport.service.dto.request;

import com.gnimty.communityapiserver.global.constant.ReportType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChampionCommentsReportServiceRequest {

	private List<ReportType> reportType;
	private String reportComment;
}
