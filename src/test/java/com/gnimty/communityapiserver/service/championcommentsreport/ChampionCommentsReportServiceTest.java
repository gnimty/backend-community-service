package com.gnimty.communityapiserver.service.championcommentsreport;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcommentsreport.entity.ChampionCommentsReport;
import com.gnimty.communityapiserver.domain.championcommentsreport.service.ChampionCommentsReportService;
import com.gnimty.communityapiserver.domain.championcommentsreport.service.dto.request.ChampionCommentsReportServiceRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.ReportType;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ChampionCommentsReportServiceTest extends ServiceTestSupport {

    @Autowired
    private ChampionCommentsReportService championCommentsReportService;

    @DisplayName("챔피언 운용법 신고 시")
    @Nested
    class DoReport {

        private Member member;
        private ChampionComments championComments;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(Member.builder()
                .upCount(0L)
                .status(Status.ONLINE)
                .nickname("nickname")
                .rsoLinked(true)
                .build());
            championComments = championCommentsRepository.save(ChampionComments.builder()
                .upCount(0L)
                .downCount(0L)
                .championId(1L)
                .contents("contents")
                .member(member)
                .depth(0)
                .version("1.1")
                .build());
            riotAccountRepository.save(RiotAccount.builder()
                .member(member)
                .name("name")
                .tagLine("tag")
                .internalTagName("name#tag")
                .isMain(true)
                .puuid("puuid")
                .level(0L)
                .build());
            MemberThreadLocal.set(member);
        }

        @DisplayName("신고를 하지 않은 상태에서 OTHER이 포함되지 않은 카테고리로 신고하면 성공한다.")
        @Test
        void should_success_when_categoryIsNotOTHER() {
            ChampionCommentsReportServiceRequest request = createRequest(List.of(ReportType.ABUSE), null);

            championCommentsReportService.doReport(1L, championComments.getId(), request);

            List<ChampionCommentsReport> championCommentsReports = championCommentsReportRepository.findAll();
            assertThat(championCommentsReports).hasSize(request.getReportType().size());
            assertThat(championCommentsReports.get(0).getReportType()).isEqualTo(request.getReportType().get(0));
            assertThat(championCommentsReports.get(0).getReportComment()).isNull();
        }

        @DisplayName("신고를 하지 않은 상태에서 OTHER 카테고리가 포함된 신고는 반드시 상세 신고 사유가 필요하다.")
        @Test
        void should_mustExistReportComments_when_categoryIsOTHER() {
            ChampionCommentsReportServiceRequest request = createRequest(List.of(ReportType.OTHER), "dd");

            championCommentsReportService.doReport(1L, championComments.getId(), request);

            List<ChampionCommentsReport> championCommentsReports = championCommentsReportRepository.findAll();
            assertThat(championCommentsReports).hasSize(request.getReportType().size());
            assertThat(championCommentsReports.get(0).getReportType()).isEqualTo(request.getReportType().get(0));
            assertThat(championCommentsReports.get(0).getReportComment()).isEqualTo(request.getReportComment());
        }

        @DisplayName("OTHER 카테고리가 포함되지 않은 신고는 상세 신고 사유를 적을 수 없다.")
        @Test
        void should() {
            ChampionCommentsReportServiceRequest request = createRequest(List.of(ReportType.ABUSE), "dd");

            BaseException exception = new BaseException(OTHER_TYPE_MUST_CONTAIN_COMMENT);
            assertThatThrownBy(() -> championCommentsReportService.doReport(1L, championComments.getId(), request))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        @DisplayName("RSO 연동이 안 된 사용자는 신고가 불가능하다.")
        @Test
        void should_unavailableReport_when_notLinkedRSO() {
            ChampionCommentsReportServiceRequest request = createRequest(List.of(ReportType.ABUSE), null);
            member.updateRsoLinked(false);

            BaseException exception = new BaseException(NOT_LINKED_RSO);
            assertThatThrownBy(() -> championCommentsReportService.doReport(1L, championComments.getId(), request))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        @DisplayName("이미 신고를 한 댓글은 또다시 신고할 수 없다.")
        @Test
        void should_fail_when_duplicateReport() {
            ChampionCommentsReportServiceRequest request = createRequest(List.of(ReportType.ABUSE), null);

            BaseException exception = new BaseException(DUPLICATED_REPORT);
            championCommentsReportService.doReport(1L, championComments.getId(), request);
            assertThatThrownBy(() -> championCommentsReportService.doReport(1L, championComments.getId(), request))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        private ChampionCommentsReportServiceRequest createRequest(List<ReportType> reportTypes, String reportComment) {
            return ChampionCommentsReportServiceRequest.builder()
                .reportType(reportTypes)
                .reportComment(reportComment)
                .build();
        }
    }
}
