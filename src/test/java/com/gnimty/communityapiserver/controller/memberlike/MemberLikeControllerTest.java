package com.gnimty.communityapiserver.controller.memberlike;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_MEMBER_LIKE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gnimty.communityapiserver.controller.ControllerTestSupport;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.memberlike.controller.dto.request.MemberLikeRequest;
import com.gnimty.communityapiserver.domain.memberlike.service.dto.request.MemberLikeServiceRequest;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;

public class MemberLikeControllerTest extends ControllerTestSupport {

	@BeforeEach
	void setUp() {
		given(tokenAuthInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any()))
			.willReturn(true);
	}

	@DisplayName("회원 좋아요 조회 시")
	@Nested
	class ReadMemberLike {

		private static final String REQUEST_URL = "/members/me/like";

		@DisplayName("현재 회원의 좋아요 수가 반환된다.")
		@Test
		void should_returnUpCount_when_invokeMethod() throws Exception {
			MemberThreadLocal.set(Member.builder().upCount(1L).build());

			mockMvc.perform(get(REQUEST_URL))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.data.upCount").value(MemberThreadLocal.get().getUpCount())
				);
			MemberThreadLocal.remove();
		}
	}

	@DisplayName("회원 좋아요 시")
	@Nested
	class DoMemberLike {

		private static final String REQUEST_URL = "/members/me/like";

		@DisplayName("올바른 요청을 하면 성공한다.")
		@Test
		void should_success_when_validRequest() throws Exception {
			MemberLikeRequest request = MemberLikeRequest.builder()
				.targetMemberId(1L)
				.cancel(false)
				.build();

			willDoNothing()
				.given(memberLikeService)
				.doMemberLike(any(MemberLikeServiceRequest.class));

			mockMvc.perform(post(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isOk(),
					jsonPath("$.status.message").value(SUCCESS_MEMBER_LIKE.getMessage())
				);
		}

		@DisplayName("targetMemberId 또는 cancel이 null이면 실패한다.")
		@ParameterizedTest
		@CsvSource({"null,1", "false,null"})
		void should_fail_when_fieldIsNull(String targetMemberIdStr, String cancelStr) throws Exception {
			Long targetMemberId = targetMemberIdStr.equals("null") ? null : 1L;
			Boolean cancel = cancelStr.equals("null") ? null : false;

			MemberLikeRequest request = MemberLikeRequest.builder()
				.targetMemberId(targetMemberId)
				.cancel(cancel)
				.build();

			willDoNothing()
				.given(memberLikeService)
				.doMemberLike(any(MemberLikeServiceRequest.class));

			mockMvc.perform(post(REQUEST_URL)
					.content(om.writeValueAsString(request))
					.contentType(MediaType.APPLICATION_JSON)
					.characterEncoding(StandardCharsets.UTF_8))
				.andExpectAll(
					status().isBadRequest(),
					jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
				);
		}
	}
}
