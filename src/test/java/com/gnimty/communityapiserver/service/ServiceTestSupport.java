package com.gnimty.communityapiserver.service;

import com.gnimty.communityapiserver.domain.block.repository.BlockRepository;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsRepository;
import com.gnimty.communityapiserver.domain.championcommentslike.repository.ChampionCommentsLikeRepository;
import com.gnimty.communityapiserver.domain.championcommentsreport.reposiotry.ChampionCommentsReportRepository;
import com.gnimty.communityapiserver.domain.introduction.repository.IntroductionRepository;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.memberlike.repository.MemberLikeRepository;
import com.gnimty.communityapiserver.domain.oauthinfo.repository.OauthInfoRepository;
import com.gnimty.communityapiserver.domain.prefergamemode.repository.PreferGameModeRepository;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountQueryRepository;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountRepository;
import com.gnimty.communityapiserver.domain.schedule.repository.ScheduleRepository;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ServiceTestSupport {

	@LocalServerPort
	public int port;
	@Autowired
	protected RiotAccountRepository riotAccountRepository;
	@Autowired
	protected OauthInfoRepository oauthInfoRepository;
	@Autowired
	protected MemberRepository memberRepository;
	@Autowired
	protected PreferGameModeRepository preferGameModeRepository;
	@Autowired
	protected MemberLikeRepository memberLikeRepository;
	@Autowired
	protected BlockRepository blockRepository;
	@Autowired
	protected ScheduleRepository scheduleRepository;
	@Autowired
	protected ChampionCommentsRepository championCommentsRepository;
	@Autowired
	protected ChampionCommentsLikeRepository championCommentsLikeRepository;
	@Autowired
	protected IntroductionRepository introductionRepository;
	@Autowired
	protected ChampionCommentsReportRepository championCommentsReportRepository;
	@Autowired
	protected RiotAccountQueryRepository riotAccountQueryRepository;

	@AfterEach
	void tearDown() {
		introductionRepository.deleteAllInBatch();
		preferGameModeRepository.deleteAllInBatch();
		scheduleRepository.deleteAllInBatch();
		blockRepository.deleteAllInBatch();
		oauthInfoRepository.deleteAllInBatch();
		memberLikeRepository.deleteAllInBatch();
		riotAccountRepository.deleteAllInBatch();
		championCommentsLikeRepository.deleteAllInBatch();
		championCommentsReportRepository.deleteAllInBatch();
		championCommentsRepository.deleteAllInBatch();
		memberRepository.deleteAllInBatch();
		MemberThreadLocal.remove();
	}
}
