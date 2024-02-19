package com.gnimty.communityapiserver.service.championcommentslike;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ALREADY_CHAMPION_COMMENTS_LIKE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.CHAMPION_COMMENTS_LIKE_NOT_FOUND;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.NOT_LINKED_RSO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcommentslike.entity.ChampionCommentsLike;
import com.gnimty.communityapiserver.domain.championcommentslike.service.ChampionCommentsLikeService;
import com.gnimty.communityapiserver.domain.championcommentslike.service.dto.request.ChampionCommentsLikeServiceRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

public class ChampionCommentsLikeServiceTest extends ServiceTestSupport {

	@Autowired
	private ChampionCommentsLikeService championCommentsLikeService;

	@DisplayName("챔피언 운용법 좋아요/싫어요 또는 취소 시")
	@Nested
	class DoChampionCommentsLike {

		private Member member;
		private ChampionComments championComments;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(Member.builder()
				.upCount(0L)
				.status(Status.ONLINE)
				.nickname("nickname")
				.rsoLinked(true)
				.build());
			championComments = championCommentsRepository.save(ChampionComments.builder()
				.upCount(0L)
				.downCount(0L)
				.championId(1L)
				.contents("contents")
				.member(member)
				.depth(0)
				.version("1.1")
				.build());
			riotAccountRepository.save(RiotAccount.builder()
				.member(member)
				.name("name")
				.tagLine("tag")
				.internalTagName("name#tag")
				.isMain(true)
				.puuid("puuid")
				.level(0L)
				.build());
			MemberThreadLocal.set(member);
		}

		@DisplayName("좋아요를 하지 않은 댓글에 좋아요를 하려 하면 성공한다.")
		@Test
		void should_success_when_like() {
			ChampionCommentsLikeServiceRequest request = createRequest(true, false);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request);

			List<ChampionCommentsLike> championCommentsLikes = championCommentsLikeRepository.findAll();

			assertThat(championCommentsLikes).hasSize(1);
			assertThat(championCommentsLikes.get(0).getChampionComments().getId()).isEqualTo(championComments.getId());
			assertThat(championCommentsLikes.get(0).getLikeOrNot()).isTrue();
			assertThat(championCommentsLikes.get(0).getDeleted()).isFalse();
			assertThat(championCommentsLikes.get(0).getMember().getId()).isEqualTo(member.getId());
		}

		@DisplayName("싫어요를 하지 않은 댓글에 싫어요를 하려 하면 성공한다.")
		@Test
		void should_success_when_dislike() {
			ChampionCommentsLikeServiceRequest request = createRequest(false, false);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request);

			List<ChampionCommentsLike> championCommentsLikes = championCommentsLikeRepository.findAll();

			assertThat(championCommentsLikes).hasSize(1);
			assertThat(championCommentsLikes.get(0).getChampionComments().getId()).isEqualTo(championComments.getId());
			assertThat(championCommentsLikes.get(0).getLikeOrNot()).isFalse();
			assertThat(championCommentsLikes.get(0).getDeleted()).isFalse();
			assertThat(championCommentsLikes.get(0).getMember().getId()).isEqualTo(member.getId());
		}

		@DisplayName("이미 좋아요를 한 댓글에 또 좋아요를 하려할 시, 실패한다.")
		@Test
		void should_fail_when_duplicateLike() {
			ChampionCommentsLikeServiceRequest request = createRequest(true, false);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request);

			BaseException exception = new BaseException(ALREADY_CHAMPION_COMMENTS_LIKE);
			assertThatThrownBy(
				() -> championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("이미 싫어요를 한 댓글에 또 싫어요를 하려할 시, 실패한다.")
		@Test
		void should_fail_when_duplicateDislike() {
			ChampionCommentsLikeServiceRequest request = createRequest(false, false);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request);

			BaseException exception = new BaseException(ALREADY_CHAMPION_COMMENTS_LIKE);
			assertThatThrownBy(
				() -> championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("좋아요를 한 댓글에 취소하려고 하면 성공한다.")
		@Test
		void should_success_when_cancelLike() {
			ChampionCommentsLikeServiceRequest request = createRequest(true, false);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request);

			ChampionCommentsLikeServiceRequest request2 = createRequest(true, true);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request2);

			assertThat(championCommentsLikeRepository.findAll()).isEmpty();
		}

		@DisplayName("싫어요를 한 댓글에 취소하려고 하면 성공한다.")
		@Test
		void should_success_when_cancelDislike() {
			ChampionCommentsLikeServiceRequest request = createRequest(false, false);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request);

			ChampionCommentsLikeServiceRequest request2 = createRequest(false, true);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request2);

			assertThat(championCommentsLikeRepository.findAll()).isEmpty();
		}

		@DisplayName("좋아요를 하지 않은 댓글에 취소하려 하면 실패한다.")
		@Test
		void should_fail_when_cancelLike() {
			ChampionCommentsLikeServiceRequest request = createRequest(true, true);

			BaseException exception = new BaseException(CHAMPION_COMMENTS_LIKE_NOT_FOUND);
			assertThatThrownBy(
				() -> championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());

		}

		@DisplayName("싫어요를 하지 않은 댓글에 취소하려 하면 실패한다.")
		@Test
		void should_fail_when_cancelDislike() {
			ChampionCommentsLikeServiceRequest request = createRequest(false, true);

			BaseException exception = new BaseException(CHAMPION_COMMENTS_LIKE_NOT_FOUND);
			assertThatThrownBy(
				() -> championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("좋아요한 댓글에 싫어요를 하거나, 싫어요한 댓글에 좋아요를 하면 실패한다.")
		@ParameterizedTest
		@CsvSource({"true,false", "false,true"})
		void should_fail_when_crossLikeOrNot(Boolean likeOrNot1, Boolean likeOrNot2) {
			ChampionCommentsLikeServiceRequest request = createRequest(likeOrNot1, false);
			championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request);

			ChampionCommentsLikeServiceRequest request2 = createRequest(likeOrNot2, false);
			BaseException exception = new BaseException(ALREADY_CHAMPION_COMMENTS_LIKE);
			assertThatThrownBy(
				() -> championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request2))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());

		}

		@DisplayName("RSO 연동이 안된 계정은 API를 사용할 수 없다.")
		@Test
		void should_cantUseAPI_when_notLinkedRSO() {
			member.updateRsoLinked(false);
			ChampionCommentsLikeServiceRequest request = createRequest(true, false);

			BaseException exception = new BaseException(NOT_LINKED_RSO);
			assertThatThrownBy(
				() -> championCommentsLikeService.doChampionCommentsLike(1L, championComments.getId(), request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		private ChampionCommentsLikeServiceRequest createRequest(Boolean likeOrNot, Boolean cancel) {
			return ChampionCommentsLikeServiceRequest.builder()
				.likeOrNot(likeOrNot)
				.cancel(cancel)
				.build();
		}
	}
}
