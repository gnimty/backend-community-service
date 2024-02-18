package com.gnimty.communityapiserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gnimty.communityapiserver.domain.block.controller.BlockController;
import com.gnimty.communityapiserver.domain.block.service.BlockReadService;
import com.gnimty.communityapiserver.domain.block.service.BlockService;
import com.gnimty.communityapiserver.domain.championcomments.controller.ChampionCommentsController;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsReadService;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsService;
import com.gnimty.communityapiserver.domain.championcommentslike.controller.ChampionCommentsLikeController;
import com.gnimty.communityapiserver.domain.championcommentslike.service.ChampionCommentsLikeService;
import com.gnimty.communityapiserver.domain.championcommentsreport.controller.ChampionCommentsReportController;
import com.gnimty.communityapiserver.domain.championcommentsreport.service.ChampionCommentsReportService;
import com.gnimty.communityapiserver.domain.chat.service.StompService;
import com.gnimty.communityapiserver.domain.chat.service.UserService;
import com.gnimty.communityapiserver.domain.introduction.service.IntroductionReadService;
import com.gnimty.communityapiserver.domain.member.controller.AuthController;
import com.gnimty.communityapiserver.domain.member.controller.MemberController;
import com.gnimty.communityapiserver.domain.member.service.AuthService;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.domain.memberlike.controller.MemberLikeController;
import com.gnimty.communityapiserver.domain.memberlike.service.MemberLikeService;
import com.gnimty.communityapiserver.domain.oauthinfo.service.OauthInfoReadService;
import com.gnimty.communityapiserver.domain.prefergamemode.service.PreferGameModeReadService;
import com.gnimty.communityapiserver.domain.riotaccount.controller.RiotAccountController;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountReadService;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountService;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleReadService;
import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.interceptor.TokenAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest(controllers = {MemberController.class, AuthController.class, BlockController.class,
    ChampionCommentsController.class, ChampionCommentsLikeController.class, ChampionCommentsReportController.class,
    MemberLikeController.class, RiotAccountController.class})
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
    protected ScheduleReadService scheduleReadService;
    @MockBean
    protected JwtProvider jwtProvider;
    @MockBean
    protected TokenAuthInterceptor tokenAuthInterceptor;
    @MockBean
    protected StompService stompService;
    @MockBean
    protected UserService userService;
    @MockBean
    protected ChampionCommentsService championCommentsService;
    @MockBean
    protected ChampionCommentsReadService championCommentsReadService;
    @MockBean
    protected ChampionCommentsLikeService championCommentsLikeService;
    @MockBean
    protected ChampionCommentsReportService championCommentsReportService;
    @MockBean
    protected MemberLikeService memberLikeService;
}
