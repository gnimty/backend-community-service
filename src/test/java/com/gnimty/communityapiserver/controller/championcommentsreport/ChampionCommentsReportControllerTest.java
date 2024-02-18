package com.gnimty.communityapiserver.controller.championcommentsreport;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_COMMENTS_REPORT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gnimty.communityapiserver.controller.ControllerTestSupport;
import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockServiceRequest;
import com.gnimty.communityapiserver.domain.championcommentsreport.controller.dto.request.ChampionCommentsReportRequest;
import com.gnimty.communityapiserver.domain.championcommentsreport.service.dto.request.ChampionCommentsReportServiceRequest;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.ReportType;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ChampionCommentsReportControllerTest extends ControllerTestSupport {

	@BeforeEach
	void setUp() {
		given(tokenAuthInterceptor.preHandle(
			any(HttpServletRequest.class),
			any(HttpServletResponse.class),
			any()))
			.willReturn(true);
		willDoNothing()
			.given(blockService)
			.doBlock(any(Member.class), any(BlockServiceRequest.class));
		willDoNothing()
			.given(stompService)
			.updateBlockStatus(any(), any(), any(Blocked.class));
	}

	@DisplayName("챔피언 운용법 신고 시")
	@Nested
	class DoReport {

		private static final String REQUEST_URL = "/champions/{champion_id}/comments/{comments_id}/reports";

		@BeforeEach
		void setUp() {
			willDoNothing()
				.given(championCommentsReportService)
				.doReport(any(Long.TYPE), any(Long.TYPE), any(ChampionCommentsReportServiceRequest.class));
		}

		@DisplayName("올바른 요청 시, 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {
			ChampionCommentsReportRequest request = createRequest(List.of(ReportType.OTHER));

			mockMvc.perform(post(REQUEST_URL, 1L, 1L)
					.content(om.writeValueAsString(request))
					.characterEncoding(StandardCharsets.UTF_8)
					.contentType(MediaType.APPLICATION_JSON))
				.andExpectAll(
					status().isCreated(),
					jsonPath("$.status.message").value(SUCCESS_COMMENTS_REPORT.getMessage())
				);
		}

		private ChampionCommentsReportRequest createRequest(List<ReportType> reportType) {
			return ChampionCommentsReportRequest.builder()
				.reportComment("comment")
				.reportType(reportType)
				.build();
		}
	}
}