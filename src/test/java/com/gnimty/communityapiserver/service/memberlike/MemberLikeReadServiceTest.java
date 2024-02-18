package com.gnimty.communityapiserver.service.memberlike;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.MEMBER_LIKE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.memberlike.entity.MemberLike;
import com.gnimty.communityapiserver.domain.memberlike.service.MemberLikeReadService;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberLikeReadServiceTest extends ServiceTestSupport {

    @Autowired
    private MemberLikeReadService memberLikeReadService;

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
    }

    @DisplayName("source와 target으로 조회 시")
    @Nested
    class FindBySourceAndTarget {

        @DisplayName("요청에 해당하는 MemberLike가 존재하면 성공한다.")
        @Test
        void should_success_when_existMemberLike() {
            memberLikeRepository.save(createMemberLike(member, member2));

            MemberLike find = memberLikeReadService.findBySourceAndTarget(member, member2);

            assertThat(find.getSourceMember().getId()).isEqualTo(member.getId());
            assertThat(find.getTargetMember().getId()).isEqualTo(member2.getId());
        }

        @DisplayName("요청에 해당하는 MemberLike가 존재하지 않으면 실패한다.")
        @Test
        void should_fail_when_doesNotExistMemberLike() {
            memberLikeRepository.save(createMemberLike(member, member2));

            BaseException exception = new BaseException(MEMBER_LIKE_NOT_FOUND);
            assertThatThrownBy(() -> memberLikeReadService.findBySourceAndTarget(member2, member))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }
    }

    @DisplayName("source와 target으로 존재 여부 확인 시")
    @Nested
    class ExistsBySourceAndTarget {

        @DisplayName("요청에 해당하는 MemberLike가 존재하면 성공한다.")
        @Test
        void should_returnTrue_when_existMemberLike() {
            memberLikeRepository.save(createMemberLike(member, member2));

            assertThat(memberLikeReadService.existsBySourceAndTarget(member, member2)).isTrue();
        }

        @DisplayName("요청에 해당하는 MemberLike가 존재하지 않으면 실패한다.")
        @Test
        void should_returnFalse_when_doesNotExistMemberLike() {
            memberLikeRepository.save(createMemberLike(member, member2));

            assertThat(memberLikeReadService.existsBySourceAndTarget(member2, member)).isFalse();
        }
    }

    @DisplayName("source로 조회 시")
    @Nested
    class FindBySourceMember {

        @DisplayName("요청한 회원이 좋아요 한 모든 좋아요 정보가 조회된다.")
        @Test
        void should_readMemberLike_when_invokeMethod() {
            Member member3 = memberRepository.save(Member.builder()
                .upCount(0L)
                .status(Status.ONLINE)
                .nickname("nickname3")
                .rsoLinked(true)
                .build());
            memberLikeRepository.save(createMemberLike(member3, member));
            memberLikeRepository.save(createMemberLike(member2, member3));
            memberLikeRepository.save(createMemberLike(member, member3));
            memberLikeRepository.save(createMemberLike(member, member2));

            List<MemberLike> find = memberLikeReadService.findBySourceMember(member);

            assertThat(find).hasSize(2);
        }
    }

    private MemberLike createMemberLike(Member source, Member target) {
        return MemberLike.builder()
            .sourceMember(source)
            .targetMember(target)
            .build();
    }
}
