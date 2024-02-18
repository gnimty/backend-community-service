package com.gnimty.communityapiserver.service.introduction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.introduction.service.IntroductionReadService;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class IntroductionReadServiceTest extends ServiceTestSupport {

    @Autowired
    private IntroductionReadService introductionReadService;


    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
            .upCount(0L)
            .status(Status.ONLINE)
            .nickname("nickname")
            .rsoLinked(true)
            .build());
    }

    @DisplayName("회원으로 조회 시")
    @Nested
    class FindByMember {

        @DisplayName("요청한 회원에 존재하는 모든 소개글이 조회된다.")
        @Test
        void should_readAllIntroductions_when_findByMember() {
            List<Introduction> introductionList = introductionRepository.saveAll(List.of(
                createIntroduction(true),
                createIntroduction(false),
                createIntroduction(false)
            ));

            List<Introduction> introductions = introductionReadService.findByMember(member);

            assertThat(introductions).hasSize(introductionList.size());
            assertThat(introductions.stream().filter(Introduction::getIsMain).toList()).hasSize(1);
        }
    }

    @DisplayName("id로 조회 시")
    @Nested
    class FindById {

        @DisplayName("존재하는 id의 소개글을 요청할 경우, 조회된다.")
        @Test
        void should_read_when_existIntroductionId() {
            Introduction introduction = createIntroduction(true);

            Introduction find = introductionReadService.findById(introduction.getId());

            assertThat(find.getId()).isEqualTo(introduction.getId());
        }

        @DisplayName("존재하지 않는 id의 소개글을 요청할 경우, 예외를 반환한다.")
        @Test
        void should_throwException_when_notExistIntroductionId() {
            Introduction introduction = createIntroduction(true);

            BaseException exception = new BaseException(ErrorCode.INTRODUCTION_NOT_FOUND);
            assertThatThrownBy(() -> introductionReadService.findById(introduction.getId() + 1))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }
    }

    @NotNull
    private Introduction createIntroduction(Boolean isMain) {
        return introductionRepository.save(
            Introduction.builder()
                .member(member)
                .content("content")
                .isMain(isMain)
                .build());
    }

    @DisplayName("main 개수 확인 시")
    @Nested
    class ThrowIfExceedMain {

        @DisplayName("main의 개수가 1개 초과일 경우, 예외를 반환한다.")
        @Test
        void should_throwException_when_mainExceedOne() {
            introductionRepository.saveAll(List.of(
                createIntroduction(true),
                createIntroduction(true)));

            BaseException exception = new BaseException(ErrorCode.MAIN_CONTENT_MUST_BE_ONLY);
            assertThatThrownBy(() -> introductionReadService.throwIfExceedMain(member))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        @DisplayName("main의 개수가 1개 이하일 경우, 성공한다.")
        @Test
        void should_success_when_mainLessOrEqualOne() {
            introductionRepository.saveAll(List.of(
                createIntroduction(true),
                createIntroduction(false)));

            assertThatNoException().isThrownBy(() -> introductionReadService.throwIfExceedMain(member));
        }
    }
}
