package com.gnimty.communityapiserver.service.prefergamemode;

import static org.assertj.core.api.Assertions.assertThat;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import com.gnimty.communityapiserver.domain.prefergamemode.service.PreferGameModeReadService;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class PreferGameModeReadServiceTest extends ServiceTestSupport {

	@Autowired
	private PreferGameModeReadService preferGameModeReadService;

	@DisplayName("회원으로 조회 시")
	@Nested
	class FindByMember {

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

		@Transactional
		@DisplayName("회원이 입력한 모든 선호 게임 모드가 조회된다.")
		@Test
		void should_readAllPreferGameModes_when_findByMember() {
			List<PreferGameMode> saved = preferGameModeRepository.saveAll(List.of(
				PreferGameMode.builder()
					.member(member)
					.gameMode(GameMode.RANK_SOLO)
					.build(),
				PreferGameMode.builder()
					.member(member)
					.gameMode(GameMode.RANK_FLEX)
					.build(),
				PreferGameMode.builder()
					.member(member)
					.gameMode(GameMode.BLIND)
					.build()
			));

			List<PreferGameMode> find = preferGameModeReadService.findByMember(member);

			assertThat(find).hasSize(saved.size());
			assertThat(find).containsAll(saved);
		}
	}

}
