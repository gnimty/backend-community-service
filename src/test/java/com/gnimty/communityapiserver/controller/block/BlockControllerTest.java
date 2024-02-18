package com.gnimty.communityapiserver.controller.block;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_BLOCK;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_CLEAR_BLOCK;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.gnimty.communityapiserver.controller.ControllerTestSupport;
import com.gnimty.communityapiserver.domain.block.controller.dto.request.BlockClearRequest;
import com.gnimty.communityapiserver.domain.block.controller.dto.request.BlockRequest;
import com.gnimty.communityapiserver.domain.block.controller.dto.response.BlockEntry;
import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockServiceRequest;
import com.gnimty.communityapiserver.domain.block.service.dto.response.BlockReadServiceResponse;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.Status;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;

public class BlockControllerTest extends ControllerTestSupport {

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

    @DisplayName("차단 목록 조회 시")
    @Nested
    class ReadBlocks {

        private static final String REQUEST_URL = "/members/me/block";

        @DisplayName("차단 목록 조회 시 내가 차단한 목록이 조회된다.")
        @Test
        void should_readBlockList_when_invokeMethod() throws Exception {
            BlockReadServiceResponse response = BlockReadServiceResponse.builder()
                .blocks(List.of(
                    BlockEntry.builder()
                        .id(1L)
                        .blockedId(2L)
                        .date(LocalDate.of(2022, 11, 11))
                        .memo("트롤")
                        .nickname("트롤러")
                        .status(Status.ONLINE)
                        .build()))
                .build();
            given(blockReadService.readBlocks())
                .willReturn(response);

            mockMvc.perform(get(REQUEST_URL))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.data.blocks[0].id").value(1),
                    jsonPath("$.data.blocks[0].blockedId").value(2),
                    jsonPath("$.data.blocks[0].date").value(LocalDate.of(2022, 11, 11).toString()),
                    jsonPath("$.data.blocks[0].memo").value("트롤"),
                    jsonPath("$.data.blocks[0].nickname").value("트롤러"),
                    jsonPath("$.data.blocks[0].status").value(Status.ONLINE.toString())
                );
        }
    }

    @DisplayName("차단 시")
    @Nested
    class DoBlock {

        private static final String REQUEST_URL = "/members/me/block";

        @DisplayName("차단 목록에 없는 회원을 차단 시, 성공한다.")
        @Test
        void should_success_when_notInBlockList() throws Exception {
            BlockRequest request = createRequest(1L, "memo");

            try (MockedStatic<MemberThreadLocal> ignored = mockStatic(MemberThreadLocal.class)) {
                given(MemberThreadLocal.get())
                    .willReturn(Member.builder().build());
                mockMvc.perform(post(REQUEST_URL)
                        .content(om.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                    .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status.message").value(SUCCESS_BLOCK.getMessage())
                    );
            }
        }

        @DisplayName("id에 올바른 값이 요청되지 않은 경우, 실패한다.")
        @ParameterizedTest
        @NullSource
        void should_fail_when_invalidId(Long id) throws Exception {
            BlockRequest request = createRequest(id, "memo");

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        @DisplayName("memo가 100자가 넘을 경우, 실패한다.")
        @Test
        void should_fail_when_memoSizeExceed100() throws Exception {
            BlockRequest request = createRequest(1L, "1".repeat(101));

            mockMvc.perform(post(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private BlockRequest createRequest(Long id, String memo) {
            return BlockRequest.builder()
                .id(id)
                .memo(memo)
                .build();
        }
    }

    @DisplayName("차단 해제 시")
    @Nested
    class ClearBlock {

        private static final String REQUEST_URL = "/members/me/block";

        @DisplayName("차단 목록에 있는 회원을 차단 해제 시, 성공한다.")
        @Test
        void should_success_when_inBlockList() throws Exception {
            BlockClearRequest request = createRequest(1L);

            try (MockedStatic<MemberThreadLocal> ignored = mockStatic(MemberThreadLocal.class)) {
                given(MemberThreadLocal.get())
                    .willReturn(Member.builder().build());
                mockMvc.perform(delete(REQUEST_URL)
                        .content(om.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                    .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status.message").value(SUCCESS_CLEAR_BLOCK.getMessage())
                    );
            }
        }

        @DisplayName("id에 올바른 값이 요청되지 않은 경우, 실패한다.")
        @ParameterizedTest
        @NullSource
        void should_fail_when_invalidId(Long id) throws Exception {
            BlockClearRequest request = createRequest(id);

            mockMvc.perform(delete(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private BlockClearRequest createRequest(Long id) {
            return BlockClearRequest.builder()
                .id(id)
                .build();
        }
    }

}
