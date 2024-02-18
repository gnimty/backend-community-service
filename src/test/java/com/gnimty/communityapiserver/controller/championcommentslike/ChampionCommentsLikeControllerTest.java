package com.gnimty.communityapiserver.controller.championcommentslike;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_CHAMPION_COMMENTS_LIKE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gnimty.communityapiserver.controller.ControllerTestSupport;
import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockServiceRequest;
import com.gnimty.communityapiserver.domain.championcommentslike.controller.dto.request.ChampionCommentsLikeRequest;
import com.gnimty.communityapiserver.domain.championcommentslike.service.dto.request.ChampionCommentsLikeServiceRequest;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.member.entity.Member;
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

public class ChampionCommentsLikeControllerTest extends ControllerTestSupport {

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

    @DisplayName("챔피언 운용법 좋아요/싫어요 시")
    @Nested
    class DoChampionCommentsLike {

        private static final String REQUEST_URL = "/champions/{champion_id}/comments/{comments_id}/like";

        @BeforeEach
        void setUp() {
            willDoNothing()
                .given(championCommentsLikeService)
                .doChampionCommentsLike(any(Long.TYPE), any(Long.TYPE), any(ChampionCommentsLikeServiceRequest.class));
        }

        @DisplayName("올바른 요청을 하면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            ChampionCommentsLikeRequest request = createRequest(true, true);

            mockMvc.perform(post(REQUEST_URL, 1L, 1L)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status.message").value(SUCCESS_CHAMPION_COMMENTS_LIKE.getMessage())
                );
        }

        @DisplayName("요청 필드 중 하나라도 null이면 실패한다.")
        @ParameterizedTest
        @CsvSource({"null,true", "true,null"})
        void should_fail_when_requestFieldIsNull(String likeOrNotStr, String cancelStr) throws Exception {
            Boolean likeOrNot = "null".equals(likeOrNotStr) ? null : Boolean.parseBoolean(likeOrNotStr);
            Boolean cancel = "null".equals(cancelStr) ? null : Boolean.parseBoolean(cancelStr);

            ChampionCommentsLikeRequest request = createRequest(likeOrNot, cancel);

            mockMvc.perform(post(REQUEST_URL, 1L, 1L)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private ChampionCommentsLikeRequest createRequest(Boolean likeOrNot, Boolean cancel) {
            return ChampionCommentsLikeRequest.builder()
                .likeOrNot(likeOrNot)
                .cancel(cancel)
                .build();
        }
    }
}
