package com.gnimty.communityapiserver.domain.riotaccount.controller;

import static com.gnimty.communityapiserver.global.constant.ApiSummary.GET_MAIN_SUMMONERS;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.GET_RECENTLY_SUMMONERS;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.GET_RECOMMENDED_SUMMONERS;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.UPDATE_SUMMONERS;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_SUMMONERS;
import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.chat.service.StompService;
import com.gnimty.communityapiserver.domain.chat.service.UserService;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.request.RecommendedSummonersRequest;
import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecentlySummonersResponse;
import com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response.RecommendedSummonersResponse;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountService;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecentlySummonersServiceResponse;
import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.RecommendedSummonersServiceResponse;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.ApiDescription;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/summoners", description = "라이엇 소환사 계정 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/summoners")
public class RiotAccountController {

	private final RiotAccountService riotAccountService;
	private final StompService stompService;
	private final UserService userService;

	@Operation(summary = UPDATE_SUMMONERS, description = ApiDescription.UPDATE_SUMMONERS)
	@PatchMapping
	public CommonResponse<Void> updateSummoners(@RequestBody @Valid SummonerUpdateRequest request) {
		List<RiotAccount> riotAccounts = riotAccountService.updateSummoners(request.toServiceRequest());
		if (!riotAccounts.isEmpty()) {
			stompService.createOrUpdateUser(riotAccounts);
		}
		return CommonResponse.success(SUCCESS_UPDATE_SUMMONERS, OK);
	}

	@Operation(summary = GET_RECOMMENDED_SUMMONERS, description = ApiDescription.GET_RECOMMENDED_SUMMONERS)
	@Parameter(in = ParameterIn.COOKIE, name = "accessToken", description = "인증을 위한 Access Token", required = true)
	@GetMapping
	public CommonResponse<RecommendedSummonersResponse> getRecommendedSummoners(
		@ModelAttribute @Valid RecommendedSummonersRequest request
	) {
		RecommendedSummonersServiceResponse response = riotAccountService.getRecommendedSummoners(
			request.toServiceRequest());
		return CommonResponse.success(RecommendedSummonersResponse.from(response));
	}

	@Operation(summary = GET_MAIN_SUMMONERS, description = ApiDescription.GET_MAIN_SUMMONERS)
	@Parameter(in = ParameterIn.COOKIE, name = "accessToken", description = "인증을 위한 Access Token", required = true)
	@GetMapping("/main")
	public CommonResponse<RecommendedSummonersResponse> getMainSummoners(
		@Schema(example = "RANK_SOLO", description = "조회하려는 게임 모드, not null") @RequestParam("game-mode") GameMode gameMode
	) {
		RecommendedSummonersServiceResponse response = riotAccountService.getMainSummoners(gameMode);
		return CommonResponse.success(RecommendedSummonersResponse.from(response));
	}

	@Operation(summary = GET_RECENTLY_SUMMONERS, description = ApiDescription.GET_RECENTLY_SUMMONERS)
	@Parameter(in = ParameterIn.COOKIE, name = "accessToken", description = "인증을 위한 Access Token", required = true)
	@GetMapping("/recently")
	public CommonResponse<RecentlySummonersResponse> getRecentlySummoners() {
		Member member = MemberThreadLocal.get();
		List<Long> chattedMemberIds = stompService.getChattedMemberIds(userService.getUser(member.getId()));
		RecentlySummonersServiceResponse response = riotAccountService.getRecentlySummoners(member, chattedMemberIds);
		return CommonResponse.success(RecentlySummonersResponse.from(response));
	}
}
