package com.gnimty.communityapiserver.service.riotaccount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateEntry;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SummonerUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountJdbcRepository;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountReadService;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountService;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountService.RecentMemberInfo;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecommendedSummonersServiceResponse;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleReadService;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.config.WebClientWrapper;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class RiotAccountServiceTest extends ServiceTestSupport {

    @Autowired
    private RiotAccountService riotAccountService;
    @MockBean
    private RiotAccountReadService riotAccountReadService;
    @MockBean
    private RiotAccountJdbcRepository riotAccountJdbcRepository;
    @MockBean
    private ScheduleReadService scheduleReadService;
    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClientWrapper webClientWrapper;

    @DisplayName("bulk update 시")
    @Nested
    class UpdateSummoners {

        @DisplayName("현재 riot account db에 저장된 계정만 필터링 돼 update한다.")
        @Test
        void should_updateFilteredAccount_when_updateSummoners() {
            RiotAccount riotAccount = mock(RiotAccount.class);
            SummonerUpdateServiceRequest request = mock(SummonerUpdateServiceRequest.class);
            SummonerUpdateEntry summonerUpdateEntry = mock(SummonerUpdateEntry.class);

            given(request.getSummonerUpdates())
                .willReturn(List.of(summonerUpdateEntry, summonerUpdateEntry));
            given(riotAccountReadService.existsByPuuid(anyString()))
                .willReturn(true);
            given(riotAccountReadService.findByPuuids(anyList()))
                .willReturn(List.of(riotAccount));

            List<RiotAccount> response = riotAccountService.updateSummoners(request);

            assertThat(response).hasSize(1);
            then(riotAccountJdbcRepository)
                .should(times(1))
                .processBatchUpdate(anyList(), anyList());
        }
    }

    @DisplayName("듀오 추천 소환사 조회 시")
    @Nested
    class GetRecommendedSummoners {

        @DisplayName("올바른 요청을 하면 성공한다.")
        @Test
        void should_success_when_validRequest() {
            Member member = mock(Member.class);
            MemberThreadLocal.set(member);
            RiotAccount riotAccount = mock(RiotAccount.class);
            Schedule schedule = mock(Schedule.class);
            RecommendedSummonersServiceRequest request = mock(RecommendedSummonersServiceRequest.class);
            RecommendedSummonersServiceResponse response = mock(RecommendedSummonersServiceResponse.class);

            given(riotAccountReadService.findMainAccountByMember(member)).willReturn(riotAccount);
            given(scheduleReadService.findByMember(member)).willReturn(List.of(schedule));
            given(riotAccountReadService.getRecommendedSummoners(request, riotAccount, List.of(schedule)))
                .willReturn(response);

            assertThatNoException().isThrownBy(() -> riotAccountService.getRecommendedSummoners(request));
        }
    }

    @DisplayName("메인 화면 추천 소환사 조회 시")
    @Nested
    class GetMainSummoners {

        @DisplayName("올바른 요청을 하면 성공한다.")
        @Test
        void should_success_when_validRequest() {
            Member member = mock(Member.class);
            MemberThreadLocal.set(member);
            RecommendedSummonersServiceResponse response = mock(RecommendedSummonersServiceResponse.class);
            given(riotAccountReadService.getMainSummoners(member, GameMode.RANK_SOLO)).willReturn(response);

            assertThatNoException().isThrownBy(() -> riotAccountService.getMainSummoners(GameMode.RANK_SOLO));
        }
    }

    @DisplayName("최근 플레이한 소환사 조회 시")
    @Nested
    class GetRecentlySummoners {

        @DisplayName("올바른 요청을 하면 성공한다.")
        @Test
        void should_success_when_validRequest() {
            Member member = mock(Member.class);
            RiotAccount riotAccount = mock(RiotAccount.class);
            given(riotAccountReadService.findMainAccountByMember(any(Member.class))).willReturn(riotAccount);
            given(riotAccount.getName()).willReturn("name");
            given(riotAccount.getTagLine()).willReturn("tag");
            RecentMemberInfo recentMemberInfo = mock(RecentMemberInfo.class);
            given(webClientWrapper.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(RecentMemberInfo.class)
                .block())
                .willReturn(recentMemberInfo);

            assertThatNoException().isThrownBy(
                () -> riotAccountService.getRecentlySummoners(member, Collections.emptyList()));
        }
    }
}
