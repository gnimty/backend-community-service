package com.gnimty.communityapiserver.domain.championcommentsreport.service;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.*;

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
		championCommentsReportRepository.save(
			ChampionCommentsReport.builder()
				.championComments(championComments)
				.reportType(request.getReportType())
				.reportComment(request.getReportComment())
				.member(member)
				.build());
	}

	private void validationReport(ChampionCommentsReportServiceRequest request, Member member) {
		if (!member.getRsoLinked()) {
			throw new BaseException(NOT_LINKED_RSO);
		}
		if (request.getReportType().equals(ReportType.OTHER) && request.getReportComment() == null) {
			throw new BaseException(OTHER_TYPE_MUST_CONTAIN_COMMENT);
		}
	}
}
