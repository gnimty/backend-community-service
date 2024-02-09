package com.gnimty.communityapiserver.service.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountRepository;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MemberReadServiceTest extends ServiceTestSupport {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RiotAccountRepository riotAccountRepository;
	@Autowired
	private MemberReadService memberReadService;

	@DisplayName("puuid로 upCount 조회 시")
	@Nested
	class FindUpCountByPuuid {

		private Member member;
		private RiotAccount riotAccount;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(createMember());
			riotAccount = riotAccountRepository.save(RiotAccount.builder()
				.puuid("puuid")
				.member(member)
				.name("name")
				.tagLine("tag")
				.internalTagName("name#tag")
				.isMain(true)
				.level(1L)
				.build());
		}

		@DisplayName("존재하는 puuid일 경우, 해당 member의 upCount를 반환한다..")
		@Test
		void should_returnUpCount_when_puuidIsExist() {
			assertThat(memberReadService.findUpCountByPuuid(riotAccount.getPuuid())).isEqualTo(member.getUpCount());
		}

		@DisplayName("존재하지 않는 puuid일 경우, 예외를 반환한다.")
		@Test
		void should_returnException_when_puuidIsNotExist() {
			assertThat(memberReadService.findUpCountByPuuid(riotAccount.getPuuid() + "a")).isNull();
		}

		private Member createMember() {
			return Member.builder()
				.upCount(1L)
				.status(Status.ONLINE)
				.nickname("nickname")
				.rsoLinked(true)
				.build();
		}
	}

}
