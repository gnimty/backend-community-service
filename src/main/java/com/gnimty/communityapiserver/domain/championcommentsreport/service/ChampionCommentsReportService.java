package com.gnimty.communityapiserver.domain.championcommentsreport.service;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.COMMENTS_ID_AND_CHAMPION_ID_INVALID;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.DUPLICATED_REPORT;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.NOT_LINKED_RSO;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.OTHER_TYPE_MUST_CONTAIN_COMMENT;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsReadService;
import com.gnimty.communityapiserver.domain.championcommentsreport.entity.ChampionCommentsReport;
import com.gnimty.communityapiserver.domain.championcommentsreport.reposiotry.ChampionCommentsReportQueryRepository;
import com.gnimty.communityapiserver.domain.championcommentsreport.reposiotry.ChampionCommentsReportRepository;
import com.gnimty.communityapiserver.domain.championcommentsreport.service.dto.request.ChampionCommentsReportServiceRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.ReportType;
import com.gnimty.communityapiserver.global.exception.BaseException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class ChampionCommentsReportService {

    private final ChampionCommentsReportRepository championCommentsReportRepository;
    private final ChampionCommentsReadService championCommentsReadService;
    private final ChampionCommentsReportQueryRepository championCommentsReportQueryRepository;

    public void doReport(Long championId, Long commentsId, ChampionCommentsReportServiceRequest request) {
        Member member = MemberThreadLocal.get();
        validationReport(request, member);
        ChampionComments championComments = championCommentsReadService.findById(commentsId);
        if (championCommentsReportQueryRepository.existsByMemberAndChampionComments(member, championComments)) {
            throw new BaseException(DUPLICATED_REPORT);
        }
        if (!Objects.equals(championId, championComments.getChampionId())) {
            throw new BaseException(COMMENTS_ID_AND_CHAMPION_ID_INVALID);
        }
        request.getReportType().forEach(
            reportType -> championCommentsReportRepository.save(
                ChampionCommentsReport.builder()
                    .championComments(championComments)
                    .reportType(reportType)
                    .reportComment(request.getReportComment())
                    .member(member)
                    .build())
        );
    }

    private void validationReport(ChampionCommentsReportServiceRequest request, Member member) {
        if (!member.getRsoLinked()) {
            throw new BaseException(NOT_LINKED_RSO);
        }
        if (isInvalidReportRelation(request)) {
            throw new BaseException(OTHER_TYPE_MUST_CONTAIN_COMMENT);
        }
    }

    private boolean isInvalidReportRelation(ChampionCommentsReportServiceRequest request) {
        return (request.getReportType().contains(ReportType.OTHER) && request.getReportComment() == null)
            || (!request.getReportType().contains(ReportType.OTHER) && request.getReportComment() != null);
    }
}
