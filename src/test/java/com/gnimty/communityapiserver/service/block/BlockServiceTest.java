package com.gnimty.communityapiserver.service.block;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ALREADY_BLOCKED_MEMBER;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.BLOCK_NOT_FOUND;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.NOT_ALLOWED_SELF_BLOCK;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.NO_PERMISSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.gnimty.communityapiserver.domain.block.entity.Block;
import com.gnimty.communityapiserver.domain.block.service.BlockReadService;
import com.gnimty.communityapiserver.domain.block.service.BlockService;
import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockClearServiceRequest;
import com.gnimty.communityapiserver.domain.block.service.dto.request.BlockServiceRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class BlockServiceTest extends ServiceTestSupport {

	@Autowired
	private BlockService blockService;
	@MockBean
	private MemberReadService memberReadService;
	@MockBean
	private BlockReadService blockReadService;

	@DisplayName("회원 차단 시")
	@Nested
	class DoBlock {

		private Member blocker;
		private Member blocked;

		@BeforeEach
		void setUp() {
			blocker = memberRepository.save(createMember("email@email.com", "nick1"));
			blocked = memberRepository.save(createMember("email@email2.com", "nick2"));
		}

		@DisplayName("차단 목록에 없는 회원을 차단 시, 성공한다.")
		@Test
		void should_success_when_notExistBlockList() {
			BlockServiceRequest request = BlockServiceRequest.builder()
				.id(blocked.getId())
				.memo("트롤")
				.build();

			given(memberReadService.findById(anyLong()))
				.willReturn(blocked);
			given(blockReadService.existsByBlockerAndBlocked(blocker, blocked))
				.willReturn(false);

			blockService.doBlock(blocker, request);
			Block block = blockRepository.findAll().get(0);

			assertThat(block.getBlocked().getId()).isEqualTo(blocked.getId());
			assertThat(block.getBlocker().getId()).isEqualTo(blocker.getId());
			assertThat(block.getMemo()).isEqualTo(request.getMemo());
		}

		@DisplayName("자기 자신 차단은 허용되지 않는다.")
		@Test
		void should_notAllowed_when_selfBlock() {
			BlockServiceRequest request = BlockServiceRequest.builder()
				.id(blocker.getId())
				.memo("트롤")
				.build();
			BaseException exception = new BaseException(NOT_ALLOWED_SELF_BLOCK);

			assertThatThrownBy(() -> blockService.doBlock(blocker, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("이미 차단 목록에 존재하는 회원인 경우, 실패한다.")
		@Test
		void should_fail_when_alreadyBlocked() {
			blockRepository.save(Block.builder()
				.blocker(blocker)
				.blocked(blocked)
				.build());
			BlockServiceRequest request = BlockServiceRequest.builder()
				.id(blocked.getId())
				.memo("트롤")
				.build();
			BaseException exception = new BaseException(ALREADY_BLOCKED_MEMBER);

			given(memberReadService.findById(blocked.getId()))
				.willReturn(blocked);
			given(blockReadService.existsByBlockerAndBlocked(any(Member.class), any(Member.class)))
				.willReturn(true);

			assertThatThrownBy(() -> blockService.doBlock(blocker, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}
	}

	@DisplayName("회원 차단 해제 시")
	@Nested
	class ClearBlock {

		private Member blocker;
		private Block block;

		@BeforeEach
		void setUp() {
			blocker = memberRepository.save(createMember("email@email.com", "nick1"));
			Member blocked = memberRepository.save(createMember("email@email2.com", "nick2"));
			block = blockRepository.save(Block.builder()
				.blocker(blocker)
				.blocked(blocked)
				.memo(" ")
				.build());
		}

		@DisplayName("올바른 차단 id를 요청하면 성공한다.")
		@Test
		void should_success_when_validBlockId() {
			BlockClearServiceRequest request = BlockClearServiceRequest.builder()
				.id(block.getId())
				.build();

			given(blockReadService.findById(request.getId()))
				.willReturn(block);

			blockService.clearBlock(blocker, request);

			assertThat(blockRepository.findAll()).isEmpty();
		}

		@DisplayName("차단 id로 차단 정보를 찾을 수 없을 시, 실패한다.")
		@Test
		void should_fail_when_notFoundBlock() {
			BlockClearServiceRequest request = BlockClearServiceRequest.builder()
				.id(block.getId())
				.build();
			BaseException exception = new BaseException(BLOCK_NOT_FOUND);

			given(blockReadService.findById(anyLong()))
				.willThrow(exception);

			assertThatThrownBy(() -> blockService.clearBlock(blocker, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("조회된 차단 정보의 차단한 사람의 id가 로그인한 회원의 id와 다르면 실패한다.")
		@Test
		void should_fail_when_foundBlockerIdNotEqualMemberId() {
			Member anotherMember = memberRepository.save(createMember("email@email3.com", "nick3"));
			BlockClearServiceRequest request = BlockClearServiceRequest.builder()
				.id(block.getId())
				.build();
			BaseException exception = new BaseException(NO_PERMISSION);

			Block anotherBlock = mock(Block.class);
			given(anotherBlock.getBlocker())
				.willReturn(anotherMember);
			given(blockReadService.findById(anyLong()))
				.willReturn(anotherBlock);

			assertThatThrownBy(() -> blockService.clearBlock(blocker, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}


	}

	private Member createMember(String email, String nickname) {
		return Member.builder()
			.email(email)
			.rsoLinked(false)
			.nickname(nickname)
			.status(Status.ONLINE)
			.upCount(1L)
			.password("password")
			.build();
	}
}
