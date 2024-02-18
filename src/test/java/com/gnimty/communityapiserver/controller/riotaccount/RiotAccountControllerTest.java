package com.gnimty.communityapiserver.controller.riotaccount;

import com.gnimty.communityapiserver.controller.ControllerTestSupport;
import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockServiceRequest;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateEntry;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SummonerUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecentlySummonersServiceResponse;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecommendedSummonersServiceResponse;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_SUMMONERS;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.MISSING_REQUEST_PARAMETER;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RiotAccountControllerTest extends ControllerTestSupport {

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

    @DisplayName("bulk update 시")
    @Nested
    class UpdateSummoners {

        public static final String REQUEST_URL = "/summoners";

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {
            SummonerUpdateRequest request = createRequest();

            given(riotAccountService.updateSummoners(any(SummonerUpdateServiceRequest.class)))
                .willReturn(Collections.emptyList());

            mockMvc.perform(patch(REQUEST_URL)
                    .content(om.writeValueAsString(request))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.status.message").value(SUCCESS_UPDATE_SUMMONERS.getMessage())
                );
        }

        private SummonerUpdateRequest createRequest() {
            return SummonerUpdateRequest.builder()
                .summonerUpdates(List.of(SummonerUpdateEntry.builder()
                    .name("name")
                    .internalTagName("name#tag")
                    .tagLine("tag")
                    .puuid("puuid")
                    .tier(Tier.bronze)
                    .division(1)
                    .lp(100L)
                    .mmr(100L)
                    .mostLanes(List.of(Lane.JUNGLE))
                    .mostChampionIds(List.of(1L, 2L, 3L))
                    .iconId(1L)
                    .tierFlex(Tier.bronze)
                    .lpFlex(100L)
                    .mmrFlex(100L)
                    .mostLanesFlex(List.of(Lane.JUNGLE))
                    .mostChampionIdsFlex(List.of(1L, 2L, 3L))
                    .build()))
                .build();
        }
    }

    @DisplayName("듀오 추천 소환사 조회 시")
    @Nested
    class GetRecommendedSummoners {

        public static final String REQUEST_URL = "/summoners";

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @ParameterizedTest
        @MethodSource({"recommendedRequestArguments"})
        void should_success_when_validRequest(SortBy sortBy, String name, Long mmr, Long upCount) throws Exception {
            RecommendedSummonersServiceResponse response = RecommendedSummonersServiceResponse.builder()
                .recommendedSummoners(Collections.emptyList())
                .build();
            given(riotAccountService.getRecommendedSummoners(any(RecommendedSummonersServiceRequest.class)))
                .willReturn(response);

            mockMvc.perform(get(REQUEST_URL)
                    .param("gameMode", String.valueOf(GameMode.RANK_SOLO))
                    .param("status", String.valueOf(Status.ONLINE))
                    .param("sortBy", String.valueOf(sortBy))
                    .param("lastSummonerId", String.valueOf(1L))
                    .param("lastName", name)
                    .param("lastSummonerMmr", mmr == null ? null : String.valueOf(mmr))
                    .param("lastSummonerUpCount", upCount == null ? null : String.valueOf(upCount))
                    .param("pageSize", "10"))
                .andExpectAll(
                    status().isOk()
                );
        }

        @DisplayName("id가 null이면 실패한다.")
        @Test
        void should_fail_when_idIsNull() throws Exception {
            RecommendedSummonersServiceResponse response = RecommendedSummonersServiceResponse.builder()
                .recommendedSummoners(Collections.emptyList())
                .build();
            given(riotAccountService.getRecommendedSummoners(any(RecommendedSummonersServiceRequest.class)))
                .willReturn(response);

            mockMvc.perform(get(REQUEST_URL)
                    .param("gameMode", String.valueOf(GameMode.RANK_SOLO))
                    .param("status", String.valueOf(Status.ONLINE))
                    .param("sortBy", String.valueOf(SortBy.RECOMMEND))
                    .param("lastSummonerUpCount", "100")
                    .param("pageSize", "10"))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        @DisplayName("validateCursor를 통과하지 못하면 실패한다.")
        @ParameterizedTest
        @EnumSource(value = SortBy.class)
        void should_fail_when_failValidateCursor(SortBy sortBy) throws Exception {
            RecommendedSummonersServiceResponse response = RecommendedSummonersServiceResponse.builder()
                .recommendedSummoners(Collections.emptyList())
                .build();
            given(riotAccountService.getRecommendedSummoners(any(RecommendedSummonersServiceRequest.class)))
                .willReturn(response);

            mockMvc.perform(get(REQUEST_URL)
                    .param("gameMode", String.valueOf(GameMode.RANK_SOLO))
                    .param("status", String.valueOf(Status.ONLINE))
                    .param("sortBy", sortBy.name())
                    .param("pageSize", "10"))
                .andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.status.message").value(INVALID_INPUT_VALUE)
                );
        }

        private static Stream<Arguments> recommendedRequestArguments() {
            return Stream.of(
                Arguments.arguments(SortBy.RECOMMEND, null, null, 10L),
                Arguments.arguments(SortBy.ATOZ, "name", null, null),
                Arguments.arguments(SortBy.TIER, null, 10L, null)
            );
        }
    }

    @DisplayName("메인 화면에서 추천 소환사 조회 시")
    @Nested
    class GetMainSummoners {

        public static final String REQUEST_URL = "/summoners/main";

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {

            RecommendedSummonersServiceResponse response = mock(RecommendedSummonersServiceResponse.class);
            given(riotAccountService.getMainSummoners(any(GameMode.class)))
                .willReturn(response);

            mockMvc.perform(get(REQUEST_URL)
                    .param("game-mode", GameMode.RANK_SOLO.name()))
                .andExpectAll(
                    status().isOk()
                );
        }

        @DisplayName("game mode가 null이면 실패한다")
        @Test
        void should_fail_when_gameModeIsnull() throws Exception {

            RecommendedSummonersServiceResponse response = mock(RecommendedSummonersServiceResponse.class);
            given(riotAccountService.getMainSummoners(any(GameMode.class)))
                .willReturn(response);

            mockMvc.perform(get(REQUEST_URL))
                .andExpectAll(
                    status().isNotFound(),
                    jsonPath("$.status.message").value(String.format(MISSING_REQUEST_PARAMETER, "game-mode"))
                );
        }
    }

    @DisplayName("최근 같이 플레이한 소환사 조회 시")
    @Nested
    class GetRecentlySummoners {

        public static final String REQUEST_URL = "/summoners/recently";

        @DisplayName("올바른 요청을 보내면 성공한다.")
        @Test
        void should_success_when_validRequest() throws Exception {
            Member member = mock(Member.class);
            MemberThreadLocal.set(member);
            User user = mock(User.class);
            RecentlySummonersServiceResponse response = mock(RecentlySummonersServiceResponse.class);

            given(member.getId()).willReturn(1L);
            given(stompService.getChattedMemberIds(any(User.class))).willReturn(Collections.emptyList());
            given(userService.getUser(anyLong())).willReturn(user);
            given(riotAccountService.getRecentlySummoners(any(Member.class), anyList())).willReturn(response);

            mockMvc.perform(get(REQUEST_URL))
                .andExpectAll(
                    status().isOk()
                );
        }
    }
}
