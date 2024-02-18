package com.gnimty.communityapiserver.service.riotaccount;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecommendedSummonersEntry;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountReadService;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.RecommendedSummonersServiceRequest;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.*;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.NOT_LINKED_RSO;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.RIOT_ACCOUNT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RiotAccountReadServiceTest extends ServiceTestSupport {

    @Autowired
    private RiotAccountReadService riotAccountReadService;

    @Nested
    class NotRecommendedTest {

        private RiotAccount riotAccount;
        private Member member;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(Member.builder()
                .upCount(0L)
                .email("email@email.com")
                .rsoLinked(true)
                .nickname("nick")
                .status(Status.ONLINE)
                .favoriteChampionID(1L)
                .build());
            riotAccount = riotAccountRepository.save(RiotAccount.builder()
                .name("name")
                .member(member)
                .level(1L)
                .internalTagName("name#tag")
                .tagLine("tag")
                .isMain(true)
                .puuid("puuid")
                .build());
        }

        @DisplayName("puuid로 존재 여부 확인 시")
        @Nested
        class ThrowIfExistsByPuuid {

            @DisplayName("puuid에 해당하는 계정이 존재하면 통과한다.")
            @Test
            void should_success_when_puuidIsExists() {
                assertThat(riotAccountReadService.existsByPuuid(riotAccount.getPuuid())).isTrue();
            }

            @DisplayName("puuid에 해당하는 계정이 존재하지 않으면 실패한다.")
            @Test
            void should_fail_when_puuidIsNotExists() {
                assertThat(riotAccountReadService.existsByPuuid(riotAccount.getPuuid() + "a")).isFalse();
            }
        }

        @DisplayName("member로 존재 여부 확인 시")
        @Nested
        class ExistsByMemberId {

            @DisplayName("member에 해당하는 계정이 존재하면 통과한다.")
            @Test
            void should_success_when_memberIsExists() {
                assertThat(riotAccountReadService.existsByMemberId(member)).isTrue();
            }

            @DisplayName("member에 해당하는 계정이 존재하지 않으면 실패한다.")
            @Test
            void should_fail_when_memberIsNotExists() {
                Member member2 = memberRepository.save(Member.builder()
                    .upCount(0L)
                    .email("email2@email.com")
                    .rsoLinked(true)
                    .nickname("nick2")
                    .status(Status.ONLINE)
                    .favoriteChampionID(1L)
                    .build());
                assertThat(riotAccountReadService.existsByMemberId(member2)).isFalse();
            }
        }

        @DisplayName("member로 모든 계정 조회 시")
        @Nested
        class FindByMember {

            @DisplayName("member에 해당하는 모든 계정이 조회된다.")
            @Test
            void should_allAccountsSelected_when_findBymember() {
                List<RiotAccount> riotAccounts = riotAccountReadService.findByMember(member);

                assertThat(riotAccounts).hasSize(1);
                assertThat(riotAccounts.get(0).getId()).isEqualTo(riotAccount.getId());
            }
        }

        @DisplayName("main 계정 조회 시")
        @Nested
        class FindMainAccountByMember {

            @DisplayName("main 계정이 존재하면 해당 계정을 반환한다.")
            @Test
            void should_returnMainAccount_when_exists() {
                RiotAccount mainRiotAccount = riotAccountReadService.findMainAccountByMember(member);

                assertThat(mainRiotAccount.getId()).isEqualTo(riotAccount.getId());
            }

            @DisplayName("main 계정이 존재하지않으면 예외를 반환한다.")
            @Test
            void should_returnException_when_notExists() {
                Member member2 = memberRepository.save(Member.builder()
                    .upCount(0L)
                    .email("email2@email.com")
                    .rsoLinked(true)
                    .nickname("nick2")
                    .status(Status.ONLINE)
                    .favoriteChampionID(1L)
                    .build());

                BaseException exception = new BaseException(NOT_LINKED_RSO);
                assertThatThrownBy(() -> riotAccountReadService.findMainAccountByMember(member2))
                    .isInstanceOf(exception.getClass())
                    .hasMessage(exception.getMessage());
            }
        }

        @DisplayName("id로 조회 시")
        @Nested
        class FindById {

            @DisplayName("계정이 존재하면 해당 계정을 반환한다.")
            @Test
            void should_returnAccount_when_exists() {
                RiotAccount find = riotAccountReadService.findById(riotAccount.getId());

                assertThat(find.getId()).isEqualTo(riotAccount.getId());
            }

            @DisplayName("계정이 존재하지않으면 예외를 반환한다.")
            @Test
            void should_returnException_when_notExists() {
                BaseException exception = new BaseException(RIOT_ACCOUNT_NOT_FOUND);
                assertThatThrownBy(() -> riotAccountReadService.findById(riotAccount.getId() + 1))
                    .isInstanceOf(exception.getClass())
                    .hasMessage(exception.getMessage());
            }
        }

        @DisplayName("puuid로 조회 시")
        @Nested
        class FindByPuuids {

            @DisplayName("puuid에 해당하는 계정이 존재하면 반환한다.")
            @Test
            void should_success_when_puuidIsExists() {
                List<RiotAccount> riotAccounts = riotAccountReadService.findByPuuids(List.of(riotAccount.getPuuid()));

                assertThat(riotAccounts).hasSize(1);
                assertThat(riotAccounts.get(0).getId()).isEqualTo(riotAccount.getId());
            }
        }

        @DisplayName("puuid로 존재 여부 확인 시")
        @Nested
        class ExistsByPuuid {

            @DisplayName("puuid에 해당하는 계정이 존재하면 true를 반환한다.")
            @Test
            void should_returnTrue_when_puuidIsExists() {
                assertThat(riotAccountReadService.existsByPuuid(riotAccount.getPuuid())).isTrue();
            }

            @DisplayName("puuid에 해당하는 계정이 존재하지 않으면 false를 반환한다.")
            @Test
            void should_returnFalse_when_puuidIsNotExists() {
                assertThat(riotAccountReadService.existsByPuuid(riotAccount.getPuuid() + "A")).isFalse();
            }
        }
    }

    @DisplayName("추천 소환사 조회 시")
    @Nested
    class GetRecommendedSummoners {

        private Member member;
        private Member member2;
        private Member member3;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(Member.builder()
                .email("email@email.com")
                .rsoLinked(true)
                .nickname("nick")
                .status(Status.ONLINE)
                .upCount(1L)
                .build());
            MemberThreadLocal.set(member);
            member2 = memberRepository.save(Member.builder()
                .email("email2@email.com")
                .rsoLinked(true)
                .nickname("nick2")
                .status(Status.ONLINE)
                .upCount(2L)
                .build());
            member3 = memberRepository.save(Member.builder()
                .email("email3@email.com")
                .rsoLinked(true)
                .nickname("nick3")
                .status(Status.ONLINE)
                .upCount(3L)
                .build());
            preferGameModeRepository.save(PreferGameMode.builder()
                .gameMode(GameMode.RANK_SOLO)
                .member(member)
                .build());
            preferGameModeRepository.save(PreferGameMode.builder()
                .gameMode(GameMode.RANK_SOLO)
                .member(member2)
                .build());
            preferGameModeRepository.save(PreferGameMode.builder()
                .gameMode(GameMode.RANK_SOLO)
                .member(member3)
                .build());
        }

        @DisplayName("추천순으로 조회 시, 높은 upcount부터 조회된다.")
        @Test
        void should_selectHigherUpCount_when_sortByIsRECOMMEND() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            createAndSaveRiotAccount("name2", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid2", member2);
            createAndSaveRiotAccount("name3", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid3", member3);

            RecommendedSummonersServiceRequest request = createRequest(Tier.diamond, null,
                SortBy.RECOMMEND, null, null);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).hasSize(2);
            assertThat(response.get(0).getUpCount()).isEqualTo(member3.getUpCount());
            assertThat(response.get(1).getUpCount()).isEqualTo(member2.getUpCount());
        }

        @DisplayName("이름순으로 조회 시, name 순으로 조회된다.")
        @Test
        void should_selectOrderOfName_when_sortByIsATOZ() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            RiotAccount riotAccount2 = createAndSaveRiotAccount("name2", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid2", member2);
            RiotAccount riotAccount3 = createAndSaveRiotAccount("name3", true, Tier.diamond, 100L, 2, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid3", member3);

            RecommendedSummonersServiceRequest request = createRequest(Tier.diamond, null,
                SortBy.ATOZ, "AAA", null);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).hasSize(2);
            assertThat(response.get(0).getName()).isEqualTo(riotAccount2.getName());
            assertThat(response.get(1).getName()).isEqualTo(riotAccount3.getName());
        }

        @DisplayName("티어순으로 조회 시, 높은 티어순으로 조회된다.")
        @Test
        void should_selectOrderOfTier_when_sortByIsTIER() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            RiotAccount riotAccount2 = createAndSaveRiotAccount("name2", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid2", member2);
            RiotAccount riotAccount3 = createAndSaveRiotAccount("name3", true, Tier.diamond, 100L, 2, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid3", member3);

            RecommendedSummonersServiceRequest request = createRequest(Tier.diamond, null,
                SortBy.TIER, null, 0L);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).hasSize(2);
            assertThat(response.get(0).getId()).isEqualTo(riotAccount2.getId());
            assertThat(response.get(1).getId()).isEqualTo(riotAccount3.getId());
        }

        @DisplayName("introduction, schedule이 null이라도 조회된다.")
        @Test
        void should_select_when_joinerIsNull() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            RiotAccount riotAccount2 = createAndSaveRiotAccount("name2", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid2", member2);
            RiotAccount riotAccount3 = createAndSaveRiotAccount("name3", true, Tier.diamond, 100L, 2, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid3", member3);

            RecommendedSummonersServiceRequest request = createRequest(Tier.diamond, null,
                SortBy.RECOMMEND, null, null);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).hasSize(2);
            assertThat(response.get(0).getId()).isEqualTo(riotAccount3.getId());
            assertThat(response.get(1).getId()).isEqualTo(riotAccount2.getId());
        }

        @DisplayName("메인 소환사만 조회된다.")
        @Test
        void should_selectMainAccount_when_getRecommendedSummoners() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            RiotAccount riotAccount2 = createAndSaveRiotAccount("name2", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid2", member2);
            createAndSaveRiotAccount("name3", false, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid3", member2);

            RecommendedSummonersServiceRequest request = createRequest(Tier.diamond, null,
                SortBy.RECOMMEND, null, null);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getId()).isEqualTo(riotAccount2.getId());
        }

        @DisplayName("마스터 이상은 조회되지 않는다.")
        @Test
        void should_excludeMasterGoe_when_getRecommendedSummoners() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            createAndSaveRiotAccount("name2", true, Tier.master, 100L, 1, 2800L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid2", member2);
            createAndSaveRiotAccount("name3", true, Tier.master, 100L, 1, 2800L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid3", member3);

            RecommendedSummonersServiceRequest request = createRequest(Tier.diamond, null,
                SortBy.RECOMMEND, null, null);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).isEmpty();
        }

        @DisplayName("lastSummonerMmr 이상의 mmr을 가진 소환사만 조회된다.")
        @Test
        void should_lastSummonerMmrGoe_when_getRecommendedSummoners() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            RiotAccount riotAccount2 = createAndSaveRiotAccount("name2", true, Tier.diamond, 0L, 1, 2700L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid2", member2);
            createAndSaveRiotAccount("name3", true, Tier.diamond, 0L, 2, 260L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid3", member3);

            RecommendedSummonersServiceRequest request = createRequest(Tier.diamond, null,
                SortBy.TIER, null, 2601L);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getId()).isEqualTo(riotAccount2.getId());
        }

        @DisplayName("lane이 하나라도겹치면 조회된다.")
        @Test
        void should_select_when_evenOneLaneOverlap() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2600L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            RiotAccount riotAccount2 = createAndSaveRiotAccount("name2", true, Tier.diamond, 0L, 1, 2700L,
                Lane.UTILITY, Lane.MIDDLE, "puuid2", member2);
            createAndSaveRiotAccount("name3", true, Tier.diamond, 0L, 1, 2700L,
                Lane.TOP, Lane.JUNGLE, "puuid3", member3);

            RecommendedSummonersServiceRequest request = createRequest(Tier.diamond, List.of(Lane.UTILITY),
                SortBy.TIER, null, 2600L);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getId()).isEqualTo(riotAccount2.getId());
        }

        @DisplayName("duoable일 경우, 듀오 가능한 소환사만 조회된다.")
        @Test
        void should_selectPossibleToDuo_when_duoableIsTrue() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 0L, 1, 2700L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            RiotAccount riotAccount2 = createAndSaveRiotAccount("name2", true, Tier.diamond, 0L, 1, 2700L,
                Lane.UTILITY, Lane.MIDDLE, "puuid2", member2);
            createAndSaveRiotAccount("name3", true, Tier.diamond, 99L, 4, 2409L,
                Lane.TOP, Lane.JUNGLE, "puuid3", member3);

            RecommendedSummonersServiceRequest request = createRequest(Tier.bronze, null,
                SortBy.TIER, null, 0L);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, Collections.emptyList()).getRecommendedSummoners();
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getId()).isEqualTo(riotAccount2.getId());
        }

        @DisplayName("시간이 조금이라도 겹치는 소환사만 조회된다.")
        @Test
        void should_select_when_evenTimeOverlap() {
            RiotAccount main = createAndSaveRiotAccount("name1", true, Tier.diamond, 100L, 1, 2799L,
                Lane.BOTTOM, Lane.MIDDLE, "puuid1", member);
            RiotAccount riotAccount2 = createAndSaveRiotAccount("name2", true, Tier.diamond, 100L, 1, 2799L,
                Lane.UTILITY, Lane.MIDDLE, "puuid2", member2);
            createAndSaveRiotAccount("name3", true, Tier.diamond, 100L, 1, 2799L,
                Lane.TOP, Lane.JUNGLE, "puuid3", member3);

            Schedule schedule = createAndSaveSchedule(member, 5, 10);
            createAndSaveSchedule(member2, 1, 6);
            createAndSaveSchedule(member3, 11, 20);

            RecommendedSummonersServiceRequest request = createRequest(Tier.bronze, null,
                SortBy.TIER, null, 0L);
            List<RecommendedSummonersEntry> response = riotAccountReadService.getRecommendedSummoners(
                request, main, List.of(schedule)).getRecommendedSummoners();
            assertThat(response).hasSize(1);
            assertThat(response.get(0).getId()).isEqualTo(riotAccount2.getId());
        }

        private Schedule createAndSaveSchedule(Member member, int startTime, int endTime) {
            return scheduleRepository.save(Schedule.builder()
                .member(member)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(startTime)
                .endTime(endTime)
                .build());
        }

        private RecommendedSummonersServiceRequest createRequest(
            Tier tier,
            List<Lane> lanes,
            SortBy sortBy,
            String lastName,
            Long lastSummonerMmr
        ) {
            return RecommendedSummonersServiceRequest.builder()
                .gameMode(GameMode.RANK_SOLO)
                .tier(tier)
                .status(Status.ONLINE)
                .lanes(lanes)
                .preferChampionIds(List.of(1L, 2L, 3L))
                .duoable(true)
                .timeMatch(true)
                .sortBy(sortBy)
                .lastSummonerId(0L)
                .lastName(lastName)
                .lastSummonerMmr(lastSummonerMmr)
                .lastSummonerUpCount(0L)
                .pageSize(100)
                .build();
        }

        private RiotAccount createAndSaveRiotAccount(
            String name,
            Boolean isMain,
            Tier tier,
            Long lp,
            Integer division,
            Long mmr,
            Lane lane1,
            Lane lane2,
            String puuid,
            Member member
        ) {
            return riotAccountRepository.save(RiotAccount.builder()
                .name(name)
                .internalTagName(name + "#tag")
                .tagLine("tag")
                .isMain(isMain)
                .queue(tier)
                .lp(lp)
                .division(division)
                .mmr(mmr)
                .frequentLane1(lane1)
                .frequentLane2(lane2)
                .frequentChampionId1(1L)
                .puuid(puuid)
                .level(100L)
                .member(member)
                .build()
            );
        }
    }
}
