package com.gnimty.communityapiserver.service.championcommentslike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsRepository;
import com.gnimty.communityapiserver.domain.championcommentslike.entity.ChampionCommentsLike;
import com.gnimty.communityapiserver.domain.championcommentslike.repository.ChampionCommentsLikeRepository;
import com.gnimty.communityapiserver.domain.championcommentslike.service.ChampionCommentsLikeReadService;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountRepository;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChampionCommentsLikeReadServiceTest extends ServiceTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RiotAccountRepository riotAccountRepository;

	@Autowired
	private ChampionCommentsRepository championCommentsRepository;

	@Autowired
	private ChampionCommentsLikeRepository championCommentsLikeRepository;

	@Autowired
	private ChampionCommentsLikeReadService championCommentsLikeReadService;

	@DisplayName("좋아요/싫어요 존재 여부 판단 시")
	@Nested
	class ExistsByMemberAndChampionComments {

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

		@AfterEach
		void tearDown() {
			championCommentsLikeRepository.deleteAllInBatch();
			championCommentsRepository.deleteAllInBatch();
			riotAccountRepository.deleteAllInBatch();
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("좋아요/싫어요를 하지 않았으면 false가 반환된다")
		@Test
		void should_returnFalse_when_didntReaction() {
			assertThat(
				championCommentsLikeReadService.existsByMemberAndChampionComments(member, championComments)).isFalse();
		}

		@DisplayName("좋아요/싫어요를 한 댓글이라면 true가 반환된다.")
		@Test
		void should_returnTrue_when_didReaction() {
			championCommentsLikeRepository.save(ChampionCommentsLike.builder()
				.likeOrNot(true)
				.member(member)
				.championComments(championComments)
				.build());

			assertThat(
				championCommentsLikeReadService.existsByMemberAndChampionComments(member, championComments)).isTrue();
		}
	}

	@DisplayName("챔피언 운용법 좋아요 조회 시")
	@Nested
	class FindByMemberAndChampionComments {

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

		@AfterEach
		void tearDown() {
			championCommentsLikeRepository.deleteAllInBatch();
			championCommentsRepository.deleteAllInBatch();
			riotAccountRepository.deleteAllInBatch();
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("내가 좋아요/싫어요 한 정보가 조회되어야 한다.")
		@Test
		void should_readInfoLiked_when_invokeMethod() {
			ChampionCommentsLike championCommentsLike = championCommentsLikeRepository.save(
				ChampionCommentsLike.builder()
					.championComments(championComments)
					.likeOrNot(true)
					.member(member)
					.build());

			ChampionCommentsLike byMemberAndChampionComments = championCommentsLikeReadService.findByMemberAndChampionComments(
				member, championComments);

			assertThat(byMemberAndChampionComments.getChampionComments().getId()).isEqualTo(championComments.getId());
			assertThat(byMemberAndChampionComments.getMember().getId()).isEqualTo(member.getId());
			assertThat(byMemberAndChampionComments.getId()).isEqualTo(championCommentsLike.getId());
		}

		@DisplayName("내가 좋아요/싫어요 한 기록이 없다면 예외를 반환한다.")
		@Test
		void should_throwException_when_didNotLiked() {

			BaseException exception = new BaseException(ErrorCode.CHAMPION_COMMENTS_LIKE_NOT_FOUND);
			assertThatThrownBy(
				() -> championCommentsLikeReadService.findByMemberAndChampionComments(member, championComments))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

	}
}
