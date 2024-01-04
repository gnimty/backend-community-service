package com.gnimty.communityapiserver.service.memberlike;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ALREADY_MEMBER_LIKE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.MEMBER_LIKE_NOT_FOUND;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.NOT_ALLOWED_SELF_LIKE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.memberlike.entity.MemberLike;
import com.gnimty.communityapiserver.domain.memberlike.repository.MemberLikeRepository;
import com.gnimty.communityapiserver.domain.memberlike.service.MemberLikeService;
import com.gnimty.communityapiserver.domain.memberlike.service.dto.request.MemberLikeServiceRequest;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberLikeServiceTest extends ServiceTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberLikeRepository memberLikeRepository;

	@Autowired
	private MemberLikeService memberLikeService;

	@DisplayName("회원 좋아요 시")
	@Nested
	class DoMemberLike {

		private Member member;
		private Member member2;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(Member.builder()
				.upCount(0L)
				.status(Status.ONLINE)
				.nickname("nickname")
				.rsoLinked(true)
				.build());
			member2 = memberRepository.save(Member.builder()
				.upCount(0L)
				.status(Status.ONLINE)
				.nickname("nickname2")
				.rsoLinked(true)
				.build());
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			memberLikeRepository.deleteAllInBatch();
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("좋아요를 하지 않은 상태에서 좋아요 시 성공한다.")
		@Test
		void should_success_when_notLikeOrNot() {
			MemberLikeServiceRequest request = createRequest(member2.getId(), false);

			memberLikeService.doMemberLike(request);

			List<MemberLike> memberLikes = memberLikeRepository.findAll();

			assertThat(memberLikes).hasSize(1);
			assertThat(memberLikes.get(0).getSourceMember().getId()).isEqualTo(member.getId());
			assertThat(memberLikes.get(0).getTargetMember().getId()).isEqualTo(member2.getId());
		}

		@DisplayName("좋아요를 한 상태에서 좋아요 시 실패한다.")
		@Test
		void should_fail_when_likeOrNot() {
			memberLikeRepository.save(MemberLike.builder()
				.sourceMember(member)
				.targetMember(member2)
				.build());
			MemberLikeServiceRequest request = createRequest(member2.getId(), false);

			BaseException exception = new BaseException(ALREADY_MEMBER_LIKE);
			assertThatThrownBy(() -> memberLikeService.doMemberLike(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("나 자신을 좋아요 할 시 예외를 반환한다.")
		@Test
		void should_fail_when_selfLike() {
			MemberLikeServiceRequest request = createRequest(member.getId(), false);

			BaseException exception = new BaseException(NOT_ALLOWED_SELF_LIKE);
			assertThatThrownBy(() -> memberLikeService.doMemberLike(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("좋아요를 한 상태에서 좋아요 삭제 시 성공한다.")
		@Test
		void should_successDelete_when_like() {
			memberLikeRepository.save(MemberLike.builder()
				.sourceMember(member)
				.targetMember(member2)
				.build());
			MemberLikeServiceRequest request = createRequest(member2.getId(), true);

			memberLikeService.doMemberLike(request);

			assertThat(memberLikeRepository.findAll()).isEmpty();
		}

		@DisplayName("좋아요를 하지 않은 상태에서 좋아요 삭제 시 실패한다.")
		@Test
		void should_failDelete_when_notLike() {
			MemberLikeServiceRequest request = createRequest(member2.getId(), true);
			BaseException exception = new BaseException(MEMBER_LIKE_NOT_FOUND);
			assertThatThrownBy(() -> memberLikeService.doMemberLike(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		private MemberLikeServiceRequest createRequest(Long targetMemberId, Boolean cancel) {
			return MemberLikeServiceRequest.builder()
				.targetMemberId(targetMemberId)
				.cancel(cancel)
				.build();
		}
	}
}
