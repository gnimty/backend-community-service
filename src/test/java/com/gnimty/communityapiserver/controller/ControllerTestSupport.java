package com.gnimty.communityapiserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnimty.communityapiserver.domain.block.service.BlockReadService;
import com.gnimty.communityapiserver.domain.block.service.BlockService;
import com.gnimty.communityapiserver.domain.chat.service.ChatService;
import com.gnimty.communityapiserver.domain.chat.service.StompService;
import com.gnimty.communityapiserver.domain.introduction.service.IntroductionReadService;
import com.gnimty.communityapiserver.domain.member.controller.MemberController;
import com.gnimty.communityapiserver.domain.member.service.AuthService;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.domain.oauthinfo.service.OauthInfoReadService;
import com.gnimty.communityapiserver.domain.prefergamemode.service.PreferGameModeReadService;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountReadService;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountService;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleReadService;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleService;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.interceptor.MemberAuthInterceptor;
import com.gnimty.communityapiserver.global.interceptor.TokenAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {MemberController.class})
@MockBean(JpaMetamodelMappingContext.class)
public abstract class ControllerTestSupport {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper om;

	@MockBean
	protected BlockService blockService;

	@MockBean
	protected BlockReadService blockReadService;

	@MockBean
	protected IntroductionReadService introductionReadService;

	@MockBean
	protected MemberService memberService;

	@MockBean
	protected MemberReadService memberReadService;

	@MockBean
	protected AuthService authService;

	@MockBean
	protected OauthInfoReadService oauthInfoReadService;

	@MockBean
	protected PreferGameModeReadService preferGameModeReadService;

	@MockBean
	protected RiotAccountService riotAccountService;

	@MockBean
	protected RiotAccountReadService riotAccountReadService;

	@MockBean
	protected ScheduleService scheduleService;

	@MockBean
	protected ScheduleReadService scheduleReadService;

	@MockBean
	protected JwtProvider jwtProvider;

	@MockBean
	protected TokenAuthInterceptor tokenAuthInterceptor;

	@MockBean
	protected MemberAuthInterceptor memberAuthInterceptor;

	@MockBean
	protected StompService stompService;
}
