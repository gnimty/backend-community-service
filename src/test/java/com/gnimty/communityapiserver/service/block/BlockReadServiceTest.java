package com.gnimty.communityapiserver.service.block;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.BLOCK_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.gnimty.communityapiserver.domain.block.entity.Block;
import com.gnimty.communityapiserver.domain.block.service.BlockReadService;
import com.gnimty.communityapiserver.domain.block.service.dto.response.BlockReadServiceResponse;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;

public class BlockReadServiceTest extends ServiceTestSupport {

    @Autowired
    private BlockReadService blockReadService;

    @DisplayName("차단 목록 조회 시")
    @Nested
    class ReadBlocks {

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
            blockRepository.save(Block.builder()
                .blocker(blocked)
                .blocked(blocker)
                .memo(" ")
                .build());
        }

        @DisplayName("readBlocks호출 시 저장된 차단 목록이 조회된다.")
        @Test
        void should_readBlocks_when_invokeMethod() {
            try (MockedStatic<MemberThreadLocal> ignored = mockStatic(MemberThreadLocal.class)) {
                given(MemberThreadLocal.get())
                    .willReturn(blocker);

                BlockReadServiceResponse response = blockReadService.readBlocks();

                assertThat(response.getBlocks()).hasSize(1);
                assertThat(response.getBlocks().get(0).getId()).isEqualTo(block.getId());
            }
        }
    }

    @DisplayName("차단한 사람, 차단된 사람으로 조회 시")
    @Nested
    class ExistsByBlockerAndBlocked {

        private Member blocker;
        private Member blocked;

        @BeforeEach
        void setUp() {
            blocker = memberRepository.save(createMember("email@email.com", "nick1"));
            blocked = memberRepository.save(createMember("email@email2.com", "nick2"));
            blockRepository.save(Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .memo(" ")
                .build());
        }

        @DisplayName("올바른 차단된 사람, 차단한 사람 요청 시 true를 반환한다.")
        @Test
        void should_returnTrue_when_validBlockedAndBlocker() {
            assertThat(blockReadService.existsByBlockerAndBlocked(blocker, blocked)).isTrue();
        }

        @DisplayName("차단된 사람, 차단한 사람이 하나라도 일치하지 않을 경우 false를 반환한다.")
        @Test
        void should_returnFalse_when_invalidBlockedAndBlocker() {
            assertThat(blockReadService.existsByBlockerAndBlocked(blocked, blocker)).isFalse();
            assertThat(blockReadService.existsByBlockerAndBlocked(blocked, blocked)).isFalse();
            assertThat(blockReadService.existsByBlockerAndBlocked(blocker, blocker)).isFalse();
        }
    }

    @DisplayName("차단 id로 조회 시")
    @Nested
    class FindById {

        private Block block;

        @BeforeEach
        void setUp() {
            Member blocker = memberRepository.save(createMember("email@email.com", "nick1"));
            Member blocked = memberRepository.save(createMember("email@email2.com", "nick2"));
            block = blockRepository.save(Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .memo(" ")
                .build());
        }

        @DisplayName("존재하는 차단 id를 요청하면 조회된다.")
        @Test
        void should_readBlock_when_existBlockId() {
            Block findBlock = blockReadService.findById(block.getId());

            assertThat(block.getId()).isEqualTo(findBlock.getId());
        }

        @DisplayName("존재하지 않는 차단 id를 요청하면 예외가 발생한다.")
        @Test
        void should_returnException_when_notExistBlockId() {
            BaseException exception = new BaseException(BLOCK_NOT_FOUND);
            assertThatThrownBy(() -> blockReadService.findById(10000L))
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
