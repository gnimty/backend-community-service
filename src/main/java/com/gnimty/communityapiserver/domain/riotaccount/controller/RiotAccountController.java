package com.gnimty.communityapiserver.domain.riotaccount.controller;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_SUMMONERS;
import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.chat.service.StompService;
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
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.response.CommonResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("summoners")
public class RiotAccountController {

	private final RiotAccountService riotAccountService;
	private final StompService stompService;

	@PatchMapping
	public CommonResponse<Void> updateSummoners(
		@RequestBody @Valid SummonerUpdateRequest request
	) {
		List<RiotAccount> riotAccounts = riotAccountService.updateSummoners(
			request.toServiceRequest());
		stompService.createOrUpdateUser(riotAccounts);
		return CommonResponse.success(SUCCESS_UPDATE_SUMMONERS, OK);
	}

	@GetMapping
	public CommonResponse<RecommendedSummonersResponse> getRecommendedSummoners(
		@ModelAttribute @Valid RecommendedSummonersRequest request
	) {
		RecommendedSummonersServiceResponse response = riotAccountService.getRecommendedSummoners(
			request.toServiceRequest());
		return CommonResponse.success(RecommendedSummonersResponse.from(response));
	}

	@GetMapping("/main")
	public CommonResponse<RecommendedSummonersResponse> getMainSummoners(
		@RequestParam("game-mode") GameMode gameMode
	) {
		RecommendedSummonersServiceResponse response = riotAccountService.getMainSummoners(
			gameMode);
		return CommonResponse.success(RecommendedSummonersResponse.from(response));
	}

	@GetMapping("/recently")
	public CommonResponse<RecentlySummonersResponse> getRecentlySummoners() {
		Member member = MemberThreadLocal.get();
		List<Long> chattedMemberIds = stompService.getChattedMemberIds(member.getId());
		RecentlySummonersServiceResponse response = riotAccountService
			.getRecentlySummoners(member, chattedMemberIds);
		return CommonResponse.success(RecentlySummonersResponse.from(response));
	}
}
