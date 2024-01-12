package com.gnimty.communityapiserver.service.championcomments;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.INVALID_CHILD_COMMENTS;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.INVALID_VERSION;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.NOT_LINKED_RSO;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.NO_PERMISSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsRepository;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsService;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsService.VersionData;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsService.VersionInfo;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsServiceRequest;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.config.WebClientWrapper;
import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

public class ChampionCommentsServiceTest extends ServiceTestSupport {

	@Autowired
	private ChampionCommentsService championCommentsService;
	@MockBean(answer = Answers.RETURNS_DEEP_STUBS)
	private WebClientWrapper webClientWrapper;

	@Nested
	@DisplayName("챔피언 운용법 추가 시")
	class AddComments {

		private Member member;
		private VersionInfo versionInfo;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				Member.builder()
					.rsoLinked(true)
					.nickname("nickname")
					.status(Status.ONLINE)
					.upCount(0L)
					.build());
			MemberThreadLocal.set(member);

			versionInfo = new VersionInfo();
			VersionData versionData = new VersionData();
			versionData.setVersion("1.1");
			versionInfo.setData(versionData);

			given(webClientWrapper.get()
				.uri("/asset/version")
				.retrieve()
				.bodyToMono(VersionInfo.class)
				.block())
				.willReturn(versionInfo);
		}

		@DisplayName("최초 댓글 추가 시 부모 댓글로 취급된다.")
		@Test
		void should_willBeParentComments_when_firstComments() {
			ChampionCommentsServiceRequest request = createRequest(Lane.BOTTOM, 0, null, null);

			championCommentsService.addComments(1L, request);

			List<ChampionComments> championCommentsList = championCommentsRepository.findAll();
			ChampionComments championComments = championCommentsList.get(0);

			assertThat(championComments.getDepth()).isEqualTo(request.getDepth());
			assertThat(championComments.getContents()).isEqualTo(request.getContents());
			assertThat(championComments.getCommentsType()).isEqualTo(request.getCommentsType());
			assertThat(championComments.getUpCount()).isEqualTo(0);
			assertThat(championComments.getParentChampionComments()).isNull();

		}

		@DisplayName("부모 댓글에 답글을 달 경우 자식 댓글로 취급된다.")
		@Test
		void should_willBeChildComments_when_replyParentComments() {

			ChampionComments parent = championCommentsRepository.save(
				createParentComments(versionInfo.getData().getVersion()));

			ChampionCommentsServiceRequest request = createRequest(null, 1, parent.getId(), null);

			championCommentsService.addComments(1L, request);

			List<ChampionComments> championCommentsList = championCommentsRepository.findAll();
			assertThat(championCommentsList).hasSize(2); // 부모 댓글과 자식 댓글 포함
			ChampionComments childComment = championCommentsList.stream()
				.filter(comment -> comment.getDepth() == 1)
				.findFirst()
				.orElseThrow();

			assertThat(childComment.getParentChampionComments().getId()).isEqualTo(parent.getId());
			assertThat(childComment.getContents()).isEqualTo(request.getContents());
			assertThat(childComment.getDepth()).isEqualTo(request.getDepth());
			assertThat(childComment.getVersion()).isEqualTo(parent.getVersion());
		}

		@DisplayName("회원의 RSO에 연동돼 있지 않으면 실패한다.")
		@Test
		void should_fail_when_memberNotLinkedRso() {
			member.updateRsoLinked(false);
			ChampionCommentsServiceRequest request = createRequest(Lane.BOTTOM, 0, null, null);

			BaseException exception = new BaseException(NOT_LINKED_RSO);

			assertThatThrownBy(() -> championCommentsService.addComments(1L, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("언급된 회원을 찾을 수 없으면 실패한다.")
		@Test
		void should_fail_when_mentionedMemberNotFound() {
			ChampionCommentsServiceRequest request = createRequest(Lane.BOTTOM, 0, null, 2L);

			BaseException exception = new BaseException(MEMBER_NOT_FOUND);

			assertThatThrownBy(() -> championCommentsService.addComments(1L, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("자식 댓글이면서 commentsType, lane, opponentChampionId가 null이 아니면 실패한다.")
		@Test
		void should_fail_when_childCommentsUpdateCommentsTypeOrLaneOrOpponentChampionId() {
			ChampionComments parent = championCommentsRepository.save(
				createParentComments(versionInfo.getData().getVersion()));
			ChampionCommentsServiceRequest request = createRequest(Lane.BOTTOM, 1, parent.getId(), null);

			BaseException exception = new BaseException(INVALID_CHILD_COMMENTS);

			assertThatThrownBy(() -> championCommentsService.addComments(1L, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("부모 댓글의 버전과 자식 댓글의 버전이 다르면 실패한다.")
		@Test
		void should_fail_when_parentAndChildVersionIsNotSame() {

			ChampionComments parent = championCommentsRepository.save(createParentComments("1.2"));
			ChampionCommentsServiceRequest request = createRequest(null, 1, parent.getId(), null);
			BaseException exception = new BaseException(INVALID_VERSION);

			assertThatThrownBy(() -> championCommentsService.addComments(1L, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		private ChampionComments createParentComments(String version) {
			return ChampionComments.builder()
				.championId(1L)
				.commentsType(CommentsType.QUESTION)
				.contents("parent")
				.depth(0)
				.member(member)
				.version(version)
				.upCount(0L)
				.downCount(0L)
				.build();
		}

		private ChampionCommentsServiceRequest createRequest(Lane lane, int depth, Long parentCommentsId,
			Long mentionedMemberId) {
			return ChampionCommentsServiceRequest.builder()
				.depth(depth)
				.contents("contents")
				.lane(lane)
				.parentChampionCommentsId(parentCommentsId)
				.mentionedMemberId(mentionedMemberId)
				.build();
		}
	}

	@Nested
	@DisplayName("챔피언 운용법 수정 시")
	class UpdateComments {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				Member.builder()
					.rsoLinked(true)
					.nickname("nickname")
					.status(Status.ONLINE)
					.upCount(0L)
					.build());
			MemberThreadLocal.set(member);
		}

		@Transactional
		@DisplayName("자신이 작성한 댓글id, request를 요청하면 성공한다.")
		@Test
		void should_update_when_validRequest() {
			ChampionComments championComments = championCommentsRepository.save(createChampionComments(member));
			ChampionCommentsUpdateServiceRequest request = ChampionCommentsUpdateServiceRequest.builder()
				.contents("newContents")
				.build();
			championCommentsService.updateComments(1L, championComments.getId(), request);

			assertThat(championComments.getContents()).isEqualTo(request.getContents());
		}

		@DisplayName("자신이 작성한 댓글이 아니라면 실패한다.")
		@Test
		void should_throwNoPermission_when_notPermission() {
			Member newMember = memberRepository.save(Member.builder()
				.rsoLinked(true)
				.nickname("nickname2")
				.status(Status.ONLINE)
				.upCount(0L)
				.build());
			ChampionComments championComments = championCommentsRepository.save(createChampionComments(newMember));
			ChampionCommentsUpdateServiceRequest request = ChampionCommentsUpdateServiceRequest.builder()
				.contents("newContents")
				.build();

			BaseException exception = new BaseException(NO_PERMISSION);
			assertThatThrownBy(() -> championCommentsService.updateComments(1L, championComments.getId(), request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		private ChampionComments createChampionComments(Member member) {
			return ChampionComments.builder()
				.downCount(0L)
				.upCount(0L)
				.championId(1L)
				.commentsType(CommentsType.QUESTION)
				.depth(0)
				.member(member)
				.version("1.1")
				.contents("contents")
				.build();
		}
	}

	@Nested
	@DisplayName("챔피언 운용법 삭제 시")
	class DeleteComments {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				Member.builder()
					.rsoLinked(true)
					.nickname("nickname")
					.status(Status.ONLINE)
					.upCount(0L)
					.build());
			MemberThreadLocal.set(member);
		}

		@Transactional
		@DisplayName("자신이 작성한 댓글id, request를 요청하면 성공한다.")
		@Test
		void should_update_when_validRequest() {
			ChampionComments championComments = championCommentsRepository.save(createChampionComments(member));
			championCommentsService.deleteComments(1L, championComments.getId());

			assertThat(championComments.getDeleted()).isTrue();
		}

		@DisplayName("자신이 작성한 댓글이 아니라면 실패한다.")
		@Test
		void should_throwNoPermission_when_notPermission() {
			Member newMember = memberRepository.save(Member.builder()
				.rsoLinked(true)
				.nickname("nickname2")
				.status(Status.ONLINE)
				.upCount(0L)
				.build());
			ChampionComments championComments = championCommentsRepository.save(createChampionComments(newMember));

			BaseException exception = new BaseException(NO_PERMISSION);
			assertThatThrownBy(() -> championCommentsService.deleteComments(1L, championComments.getId()))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		private ChampionComments createChampionComments(Member member) {
			return ChampionComments.builder()
				.downCount(0L)
				.upCount(0L)
				.championId(1L)
				.commentsType(CommentsType.QUESTION)
				.depth(0)
				.member(member)
				.version("1.1")
				.contents("contents")
				.build();
		}
	}
}
