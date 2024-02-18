package com.gnimty.communityapiserver.controller.championcomments;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_ADD_CHAMPION_COMMENTS;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_DELETE_CHAMPION_COMMENTS;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_CHAMPION_COMMENTS;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gnimty.communityapiserver.controller.ControllerTestSupport;
import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockServiceRequest;
import com.gnimty.communityapiserver.domain.championcomments.controller.dto.request.ChampionCommentsRequest;
import com.gnimty.communityapiserver.domain.championcomments.controller.dto.request.ChampionCommentsUpdateRequest;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsServiceRequest;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsEntry;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsServiceResponse;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

public class ChampionCommentsControllerTest extends ControllerTestSupport {

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

    @Nested
    @DisplayName("챔피언 운용법 조회 시")
    class ReadChampionComments {

        private static final String REQUEST_URL = "/champions/{champion_id}/comments";

        @DisplayName("올바른 championID를 요청하면 성공한다.")
        @Test
        void should_success_when_requestValidChampionId() throws Exception {

            ChampionCommentsServiceResponse response = createResponse();

            given(championCommentsReadService.findByChampionId(any(Long.class)))
                .willReturn(response);

            String responsePath = "$.data.championComments[0].";
            mockMvc.perform(get(REQUEST_URL, 1))
                .andExpectAll(
                    status().isOk(),
                    jsonPath(responsePath + "id").value(response.getChampionComments().get(0).getId()),
                    jsonPath(responsePath + "depth").value(response.getChampionComments().get(0).getDepth()),
                    jsonPath(responsePath + "name").value(response.getChampionComments().get(0).getName()),
                    jsonPath(responsePath + "like").value(response.getChampionComments().get(0).getLike())
                );
        }

        @DisplayName("championID의 타입이 올바르지 않으면 실패한다.")
        @Test
        void should_fail_when_championIDTypeIsInvalid() throws Exception {

            ChampionCommentsServiceResponse response = createResponse();

            given(championCommentsReadService.findByChampionId(any(Long.class)))
                .willReturn(response);

            mockMvc.perform(get(REQUEST_URL, "a"))
                .andExpectAll(
                    status().isBadRequest()
                );
        }

        private ChampionCommentsServiceResponse createResponse() {
            return ChampionCommentsServiceResponse.builder()
                .championComments(List.of(ChampionCommentsEntry.builder()
                    .id(1L)
                    .lane(Lane.MIDDLE)
                    .opponentChampionId(2L)
                    .depth(1)
                    .mentionedMemberId(2L)
                    .contents("contents")
                    .commentsType(CommentsType.QUESTION)
                    .upCount(1L)
                    .downCount(1L)
                    .version("1.1.1")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .deleted(false)
                    .blocked(false)
                    .memberId(1L)
                    .name("name")
                    .tagLine("tag")
                    .like(true)
                    .build()))
                .build();
        }
    }

    @Nested
    @DisplayName("챔피언 운용법 추가 시")
    class AddChampionComments {

        private static final String REQUEST_URL = "/champions/{champion_id}/comments";

        @BeforeEach
        void setUp() {
            willDoNothing()
                .given(championCommentsService)
                .addComments(any(Long.TYPE), any(ChampionCommentsServiceRequest.class));
        }

        @DisplayName("올바른 champion_id, request를 요청하면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            ChampionCommentsRequest request = createRequest(0, "con", null);

            mockMvc.perform(post(REQUEST_URL, 1L)
                    .content(om.writeValueAsString(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isCreated(),
                    jsonPath("$.status.message").value(SUCCESS_ADD_CHAMPION_COMMENTS.getMessage())
                );
        }

        @DisplayName("depth의 크기가 올바르지 않으면 실패한다.")
        @ParameterizedTest
        @ValueSource(ints = {-1, 2, 3, 4, 55})
        void should_fail_when_invalidDepth(int depth) throws Exception {
            ChampionCommentsRequest request = createRequest(depth, "con", null);

            mockMvc.perform(post(REQUEST_URL, 1L)
                    .content(om.writeValueAsString(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        @DisplayName("contents의 크기가 올바르지 않으면 실패한다.")
        @Test
        void should_fail_when_invalidContents() throws Exception {
            ChampionCommentsRequest request = createRequest(0, "a".repeat(1001), null);

            mockMvc.perform(post(REQUEST_URL, 1L)
                    .content(om.writeValueAsString(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        @DisplayName("depth와 parentChampionCommentsId의 관계가 올바르지 않으면 실패한다.")
        @Test
        void should_fail_when_invalidDepthAndParentChampionCommentsId() throws Exception {
            ChampionCommentsRequest request = createRequest(0, "con", 1L);

            mockMvc.perform(post(REQUEST_URL, 1L)
                    .content(om.writeValueAsString(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private ChampionCommentsRequest createRequest(
            Integer depth,
            String contents,
            Long parentChampionCommentsId
        ) {
            return ChampionCommentsRequest.builder()
                .lane(Lane.MIDDLE)
                .opponentChampionId(1L)
                .depth(depth)
                .mentionedMemberId(1L)
                .contents(contents)
                .commentsType(CommentsType.QUESTION)
                .parentChampionCommentsId(parentChampionCommentsId)
                .build();
        }
    }

    @Nested
    @DisplayName("챔피언 운용법 수정 시")
    class UpdateChampionComments {

        private static final String REQUEST_URL = "/champions/{champion_id}/comments/{comments_id}";

        @BeforeEach
        void setUp() {
            willDoNothing()
                .given(championCommentsService)
                .updateComments(any(Long.TYPE), any(Long.TYPE), any(ChampionCommentsUpdateServiceRequest.class));
        }

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {
            ChampionCommentsUpdateRequest request = createRequest("contents");

            mockMvc.perform(patch(REQUEST_URL, 1L, 1L)
                    .content(om.writeValueAsString(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status.message").value(SUCCESS_UPDATE_CHAMPION_COMMENTS.getMessage())
                );
        }

        @DisplayName("contents의 크기가 올바르지 않으면 실패한다.")
        @Test
        void should_fail_when_invalidContentsSize() throws Exception {
            ChampionCommentsUpdateRequest request = createRequest("a".repeat(1001));

            mockMvc.perform(patch(REQUEST_URL, 1L, 1L)
                    .content(om.writeValueAsString(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private ChampionCommentsUpdateRequest createRequest(String contents) {
            return ChampionCommentsUpdateRequest.builder()
                .mentionedMemberId(1L)
                .contents(contents)
                .build();
        }
    }

    @Nested
    @DisplayName("챔피언 운용법 삭제 시")
    class DeleteChampionComments {

        private static final String REQUEST_URL = "/champions/{champion_id}/comments/{comments_id}";

        @BeforeEach
        void setUp() {
            willDoNothing()
                .given(championCommentsService)
                .deleteComments(any(Long.TYPE), any(Long.TYPE));
        }

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            mockMvc.perform(delete(REQUEST_URL, 1L, 1L))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status.message").value(SUCCESS_DELETE_CHAMPION_COMMENTS.getMessage())
                );
        }
    }
}
