package com.gnimty.communityapiserver.service.championcomments;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.CHAMPION_COMMENTS_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.block.entity.Block;
import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsReadService;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsEntry;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsServiceResponse;
import com.gnimty.communityapiserver.domain.championcommentslike.entity.ChampionCommentsLike;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChampionCommentsReadServiceTest extends ServiceTestSupport {

    @Autowired
    private ChampionCommentsReadService championCommentsReadService;

    @DisplayName("id로 단건 조회 시")
    @Nested
    class FindById {

        private ChampionComments championComments;

        @BeforeEach
        void setUp() {
            Member member = memberRepository.save(Member.builder()
                .upCount(1L)
                .rsoLinked(true)
                .status(Status.ONLINE)
                .nickname("nickname")
                .build());
            championComments = championCommentsRepository.save(ChampionComments.builder()
                .member(member)
                .championId(1L)
                .depth(0)
                .upCount(1L)
                .downCount(1L)
                .contents("contents")
                .version("1.1")
                .build());
        }

        @DisplayName("조회하려는 댓글이 존재하면 해당 댓글이 조회된다.")
        @Test
        void should_readComments_when_validRequest() {

            ChampionComments findChampionComments = championCommentsReadService.findById(championComments.getId());

            assertThat(findChampionComments.getId()).isEqualTo(championComments.getId());
        }

        @DisplayName("요청하는 id에 해당하는 댓글이 존재하지 않으면 예외를 반환한다.")
        @Test
        void should_throwException_when_notFoundComments() {

            BaseException exception = new BaseException(CHAMPION_COMMENTS_NOT_FOUND);
            assertThatThrownBy(() -> championCommentsReadService.findById(1000L))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }
    }

    @DisplayName("championId로 전체 조회 시")
    @Nested
    class FindByChampionId {

        private Member member1;
        private Member member2;

        @BeforeEach
        void setUp() {
            member1 = memberRepository.save(createMember("member1"));
            member2 = memberRepository.save(createMember("member2"));
            riotAccountRepository.save(createRiotAccountByNameAndPuuid("name1", "puuid1", member1));
            riotAccountRepository.save(createRiotAccountByNameAndPuuid("name2", "puuid2", member2));
            MemberThreadLocal.set(member1);
        }

        @DisplayName("championId에 맞는 댓글만 조회되어야 한다.")
        @Test
        void should_readSelectedChampionId_when_read() {
            ChampionComments championComments = championCommentsRepository.save(
                createChampionCommentsByChampionId(1L, member1));
            championCommentsRepository.save(createChampionCommentsByChampionId(2L, member1));

            ChampionCommentsServiceResponse response = championCommentsReadService.findByChampionId(1L);

            assertThat(response.getChampionComments()).hasSize(1);
            assertThat(response.getChampionComments().get(0).getId()).isEqualTo(championComments.getId());
        }

        @DisplayName("차단한 회원이 존재할 경우, blocked가 true로 조회되어야 한다.")
        @Test
        void should_readBlockedIsTrue_when_existBlockedMember() {
            blockRepository.save(Block.builder()
                .blocker(member1)
                .blocked(member2)
                .memo("memo")
                .build());
            championCommentsRepository.save(createChampionCommentsByChampionId(1L, member1));
            championCommentsRepository.save(createChampionCommentsByChampionId(1L, member2));

            ChampionCommentsServiceResponse response = championCommentsReadService.findByChampionId(1L);

            assertThat(response.getChampionComments()).hasSize(2);
            ChampionCommentsEntry blocked = response.getChampionComments().stream()
                .filter(championCommentsEntry -> championCommentsEntry.getMemberId().equals(member2.getId()))
                .findFirst()
                .orElseThrow();
            assertThat(blocked.getBlocked()).isTrue();
        }

        @DisplayName("depth -> upCount -> downCount -> createdAt 순서로 조회되어야 한다.")
        @Test
        void should_SortedState_when_read() {
            List<ChampionComments> championCommentsList = championCommentsRepository.saveAll(List.of(
                createChampionCommentsByUpAndDownCount(3L, 0L),
                createChampionCommentsByUpAndDownCount(2L, 1L),
                createChampionCommentsByUpAndDownCount(2L, 2L),
                createChampionCommentsByUpAndDownCount(2L, 2L),
                createChampionCommentsByUpAndDownCount(1L, 0L)
            ));

            ChampionCommentsServiceResponse response = championCommentsReadService.findByChampionId(1L);
            assertThat(response.getChampionComments().get(0).getId()).isEqualTo(championCommentsList.get(0).getId());
            assertThat(response.getChampionComments().get(1).getId()).isEqualTo(championCommentsList.get(1).getId());
            assertThat(response.getChampionComments().get(2).getId()).isEqualTo(championCommentsList.get(2).getId());
            assertThat(response.getChampionComments().get(3).getId()).isEqualTo(championCommentsList.get(3).getId());
            assertThat(response.getChampionComments().get(4).getId()).isEqualTo(championCommentsList.get(4).getId());
        }

        @DisplayName("내가 좋아요한 댓글의 경우, likeOrNot이 true로 조회되어야 한다.")
        @Test
        void should_likeOrNotIsTrue_when_commentsLiked() {
            ChampionComments championComments = championCommentsRepository.save(
                createChampionCommentsByChampionId(1L, member2));
            championCommentsRepository.save(createChampionCommentsByChampionId(1L, member2));
            championCommentsLikeRepository.save(ChampionCommentsLike.builder()
                .member(member1)
                .likeOrNot(true)
                .championComments(championComments)
                .build());

            ChampionCommentsServiceResponse response = championCommentsReadService.findByChampionId(1L);
            assertThat(response.getChampionComments().get(0).getLike()).isTrue();
            assertThat(response.getChampionComments().get(1).getLike()).isNull();
        }

        private Member createMember(String nickname) {
            return Member.builder()
                .nickname(nickname)
                .status(Status.ONLINE)
                .rsoLinked(true)
                .upCount(0L)
                .build();
        }

        private ChampionComments createChampionCommentsByChampionId(Long championId, Member member) {
            return ChampionComments.builder()
                .upCount(0L)
                .depth(0)
                .commentsType(CommentsType.QUESTION)
                .member(member)
                .championId(championId)
                .version("1.1")
                .downCount(0L)
                .contents("contents")
                .build();
        }

        private RiotAccount createRiotAccountByNameAndPuuid(String name, String puuid, Member member) {
            return RiotAccount.builder()
                .name(name)
                .internalTagName(name + "#tag")
                .tagLine("tag")
                .isMain(true)
                .puuid(puuid)
                .level(0L)
                .member(member)
                .build();
        }

        private ChampionComments createChampionCommentsByUpAndDownCount(Long upCount, Long downCount) {
            return ChampionComments.builder()
                .championId(1L)
                .upCount(upCount)
                .downCount(downCount)
                .depth(0)
                .commentsType(CommentsType.QUESTION)
                .member(member1)
                .version("1.1")
                .contents("contents")
                .build();
        }
    }
}
