package com.gnimty.communityapiserver.domain.championcommentsreport.service.dto.request;

import com.gnimty.communityapiserver.global.constant.ReportType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChampionCommentsReportServiceRequest {

    private List<ReportType> reportType;
    private String reportComment;
}
