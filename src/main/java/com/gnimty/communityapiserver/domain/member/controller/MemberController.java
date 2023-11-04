package com.gnimty.communityapiserver.domain.member.controller;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_DISCONNECT_OAUTH;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_GOOGLE_LINK;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_KAKAO_LINK;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_LOGOUT;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_SEND_EMAIL_AUTH_CODE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_SUMMONER_LINK;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_INTRODUCTION;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_PASSWORD;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_PREFER_GAME_MODE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_PROFILE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_UPDATE_STATUS;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_VERIFY_EMAIL;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_WITHDRAWAL;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.chat.service.ChatService;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.IntroductionUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.MyProfileUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.OauthLoginRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.PasswordEmailVerifyRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.PasswordUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.PreferGameModeUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.StatusUpdateRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.response.MyProfileResponse;
import com.gnimty.communityapiserver.domain.member.controller.dto.response.OtherProfileResponse;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.domain.member.service.dto.response.MyProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OtherProfileServiceResponse;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

	private final MemberService memberService;
	private final MemberReadService memberReadService;
	private final ChatService chatService;

	@PostMapping("/{member_id}/rso")
	public CommonResponse<Void> summonerAccountLink(
		@RequestBody @Valid OauthLoginRequest request
	) {
		RiotAccount riotAccount = memberService.summonerAccountLink(request.toServiceRequest());
		chatService.createOrUpdateUser(riotAccount);
		return CommonResponse.success(SUCCESS_SUMMONER_LINK, OK);
	}

	@PostMapping("/{member_id}/oauth/kakao")
	public CommonResponse<Void> kakaoAdditionalLink(
		@RequestBody @Valid OauthLoginRequest request
	) {
		memberService.oauthAdditionalLink(Provider.KAKAO, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_KAKAO_LINK, OK);
	}

	@PostMapping("/{member_id}/oauth/google")
	public CommonResponse<Void> googleAdditionalLink(
		@RequestBody @Valid OauthLoginRequest request
	) {
		memberService.oauthAdditionalLink(Provider.GOOGLE, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_GOOGLE_LINK, OK);
	}

	@GetMapping("/me")
	public CommonResponse<MyProfileResponse> getMyProfile() {
		MyProfileServiceResponse response = memberService.getMyProfile();
		return CommonResponse.success(MyProfileResponse.from(response));
	}

	@PatchMapping("/{member_id}")
	public CommonResponse<Void> updateMyProfile(
		@PathVariable("member_id") Long memberId,
		@RequestBody @Valid MyProfileUpdateRequest request
	) {
		RiotAccount riotAccount = memberService.updateMyProfile(memberId, request.toServiceRequest());
		if (request.getStatus() != null) {
			chatService.updateConnStatus(chatService.getUser(memberId), request.getStatus());
		}
		if (riotAccount != null) {
			chatService.createOrUpdateUser(riotAccount);
		}
		return CommonResponse.success(SUCCESS_UPDATE_PROFILE, OK);
	}

	@ResponseStatus(ACCEPTED)
	@PostMapping("/{member_id}/password/email")
	public CommonResponse<Void> sendEmailAuthCode() {
		memberService.sendEmailAuthCode(MemberThreadLocal.get());
		return CommonResponse.success(SUCCESS_SEND_EMAIL_AUTH_CODE, ACCEPTED);
	}

	@PostMapping("/{member_id}/password/email/code")
	public CommonResponse<Void> verifyEmailAuthCode(
		@RequestBody @Valid PasswordEmailVerifyRequest request
	) {
		memberService.verifyEmailAuthCode(request.toServiceRequest());
		return CommonResponse.success(SUCCESS_VERIFY_EMAIL, OK);
	}

	@PatchMapping("/{member_id}/password")
	public CommonResponse<Void> updatePassword(
		@PathVariable("member_id") Long memberId,
		@RequestBody @Valid PasswordUpdateRequest request
	) {
		memberService.updatePassword(memberId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_UPDATE_PASSWORD, OK);
	}

	@PatchMapping("/{member_id}/status")
	public CommonResponse<Void> updateStatus(
		@PathVariable("member_id") Long memberId,
		@RequestBody @Valid StatusUpdateRequest request
	) {
		memberService.updateStatus(memberId, request.toServiceRequest());
		chatService.updateConnStatus(chatService.getUser(memberId), request.getStatus());
		return CommonResponse.success(SUCCESS_UPDATE_STATUS, OK);
	}

	@PatchMapping("/{member_id}/introductions")
	public CommonResponse<Void> updateIntroduction(
		@PathVariable("member_id") Long memberId,
		@RequestBody @Valid IntroductionUpdateRequest request
	) {
		memberService.updateIntroduction(memberId, request.toServiceRequest());
		return CommonResponse.success(SUCCESS_UPDATE_INTRODUCTION, OK);
	}

	@PatchMapping("/{member_id}/prefer-game-mode")
	public CommonResponse<Void> updatePreferGameMode(
		@RequestBody @Valid PreferGameModeUpdateRequest request
	) {
		memberService.updatePreferGameMode(request.toServiceRequest());
		return CommonResponse.success(SUCCESS_UPDATE_PREFER_GAME_MODE, OK);
	}

	@DeleteMapping("/{member_id}/oauth")
	public CommonResponse<Void> deleteOauthInfo(@RequestParam Provider provider) {
		memberService.deleteOauthInfo(provider);
		return CommonResponse.success(SUCCESS_DISCONNECT_OAUTH, OK);
	}

	@DeleteMapping("/{member_id}/logout")
	public CommonResponse<Void> logout() {
		memberService.logout();
		return CommonResponse.success(SUCCESS_LOGOUT, OK);
	}

	@DeleteMapping("/{member_id}")
	public CommonResponse<Void> withdrawal() {
		memberService.withdrawal();
		return CommonResponse.success(SUCCESS_WITHDRAWAL, OK);
	}

	@GetMapping("/{member_id}")
	public CommonResponse<OtherProfileResponse> getOtherProfile(
		@PathVariable("member_id") Long memberId
	) {
		OtherProfileServiceResponse response = memberReadService.findOtherById(memberId);
		return CommonResponse.success(OtherProfileResponse.from(response));
	}
}
