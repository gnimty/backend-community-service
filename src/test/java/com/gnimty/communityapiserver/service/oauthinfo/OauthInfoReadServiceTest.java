package com.gnimty.communityapiserver.service.oauthinfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.oauthinfo.entity.OauthInfo;
import com.gnimty.communityapiserver.domain.oauthinfo.service.OauthInfoReadService;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class OauthInfoReadServiceTest extends ServiceTestSupport {

    @Autowired
    private OauthInfoReadService oauthInfoReadService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.builder()
            .rsoLinked(true)
            .nickname("nickname")
            .status(Status.ONLINE)
            .upCount(0L)
            .build());
    }

    @DisplayName("email과 provider로 조회 시")
    @Nested
    class ThrowIfExistsByEmailAndProvider {

        @DisplayName("존재하지 않는 OauthInfo이면 성공한다.")
        @Test
        void should_success_when_notExistOauthInfo() {
            OauthInfo oauthInfo = oauthInfoRepository.save(OauthInfo.builder()
                .member(member)
                .provider(Provider.KAKAO)
                .email("email@email.com")
                .build());

            assertThatNoException().isThrownBy(
                () -> oauthInfoReadService.throwIfExistsByEmailAndProvider(oauthInfo.getEmail(), Provider.GOOGLE));
            assertThatNoException().isThrownBy(
                () -> oauthInfoReadService.throwIfExistsByEmailAndProvider("email2@email.com", Provider.KAKAO));
        }

        @DisplayName("존재하는 OauthInfo이면 예외를 반환한다.")
        @Test
        void should_fail_when_existOauthInfo() {
            OauthInfo oauthInfo = oauthInfoRepository.save(OauthInfo.builder()
                .member(member)
                .provider(Provider.KAKAO)
                .email("email@email.com")
                .build());

            BaseException exception = new BaseException(ErrorCode.ALREADY_LINKED_OAUTH);
            assertThatThrownBy(() -> oauthInfoReadService.throwIfExistsByEmailAndProvider(oauthInfo.getEmail(),
                oauthInfo.getProvider()))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }
    }

    @DisplayName("회원으로 조회 시")
    @Nested
    class FindByMember {

        @DisplayName("존재하는 모든 OauthInfo가 조회된다.")
        @Transactional
        @Test
        void should_selectAllOauthInfos_when_findByMember() {
            List<OauthInfo> oauthInfos = oauthInfoRepository.saveAll(List.of(
                OauthInfo.builder()
                    .member(member)
                    .email("email@email.com")
                    .provider(Provider.GOOGLE)
                    .build(),
                OauthInfo.builder()
                    .member(member)
                    .email("email@email.com")
                    .provider(Provider.KAKAO)
                    .build()
            ));

            List<OauthInfo> find = oauthInfoReadService.findByMember(member);

            assertThat(find).hasSize(oauthInfos.size());
            assertThat(find).containsAll(oauthInfos);
        }
    }

    @DisplayName("member와 provider로 존재 여부 판단 시")
    @Nested
    class ExistsByMemberAndProvider {

        @DisplayName("존재하지 않는 OauthInfo이면 false를 반환한다.")
        @Test
        void should_success_when_notExistOauthInfo() {
            oauthInfoRepository.save(OauthInfo.builder()
                .member(member)
                .provider(Provider.KAKAO)
                .email("email@email.com")
                .build());

            assertThat(oauthInfoReadService.existsByMemberAndProvider(member, Provider.GOOGLE)).isFalse();
        }

        @DisplayName("존재하는 OauthInfo이면 true를 반환한다.")
        @Test
        void should_fail_when_existOauthInfo() {
            OauthInfo oauthInfo = oauthInfoRepository.save(OauthInfo.builder()
                .member(member)
                .provider(Provider.KAKAO)
                .email("email@email.com")
                .build());

            assertThat(oauthInfoReadService.existsByMemberAndProvider(member, oauthInfo.getProvider())).isTrue();
        }
    }

    @DisplayName("member와 provider로 조회 시")
    @Nested
    class FindByMemberAndProvider {

        @DisplayName("존재하지 않는 OauthInfo이면 예외를 반환한다.")
        @Test
        void should_throwException_when_notExistOauthInfo() {
            oauthInfoRepository.save(OauthInfo.builder()
                .member(member)
                .provider(Provider.KAKAO)
                .email("email@email.com")
                .build());

            BaseException exception = new BaseException(ErrorCode.OAUTH_INFO_NOT_FOUND);
            assertThatThrownBy(() -> oauthInfoReadService.findByMemberAndProvider(member, Provider.GOOGLE))
                .isInstanceOf(exception.getClass())
                .hasMessage(exception.getMessage());
        }

        @DisplayName("존재하는 OauthInfo이면 해당 OauthInfo를 반환한다.")
        @Test
        void should_returnOauthInfo_when_findByMemberAndProvider() {
            OauthInfo save = oauthInfoRepository.save(OauthInfo.builder()
                .member(member)
                .provider(Provider.KAKAO)
                .email("email@email.com")
                .build());

            OauthInfo find = oauthInfoReadService.findByMemberAndProvider(member, save.getProvider());
            assertThat(find.getId()).isEqualTo(save.getId());
        }
    }
}
