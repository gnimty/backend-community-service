package com.gnimty.communityapiserver.domain.member.controller;

import com.gnimty.communityapiserver.domain.chat.service.StompService;
import com.gnimty.communityapiserver.domain.chat.service.UserService;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.*;
import com.gnimty.communityapiserver.domain.member.controller.dto.response.MyProfileResponse;
import com.gnimty.communityapiserver.domain.member.controller.dto.response.OtherProfileResponse;
import com.gnimty.communityapiserver.domain.member.controller.dto.response.PasswordEmailVerifyResponse;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.domain.member.service.dto.response.MyProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OtherProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PasswordEmailVerifyServiceResponse;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.ApiDescription;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.gnimty.communityapiserver.global.constant.ApiSummary.*;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.*;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@Tag(name = "/members", description = "회원 정보 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberReadService memberReadService;
    private final StompService stompService;
    private final UserService userService;

    @Operation(summary = SUMMONER_ACCOUNT_LINK, description = ApiDescription.SUMMONER_ACCOUNT_LINK)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @PostMapping("/me/rso")
    public CommonResponse<Void> summonerAccountLink(@RequestBody @Valid OauthLoginRequest request) {
        RiotAccount riotAccount = memberService.summonerAccountLink(request.toServiceRequest());
        stompService.createOrUpdateUser(riotAccount);
        return CommonResponse.success(SUCCESS_SUMMONER_LINK, OK);
    }

    @Operation(summary = KAKAO_ADDITIONAL_LINK, description = ApiDescription.KAKAO_ADDITIONAL_LINK)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @PostMapping("/me/oauth/kakao")
    public CommonResponse<Void> kakaoAdditionalLink(@RequestBody @Valid OauthLoginRequest request) {
        memberService.oauthAdditionalLink(Provider.KAKAO, request.toServiceRequest());
        return CommonResponse.success(SUCCESS_KAKAO_LINK, OK);
    }

    @Operation(summary = GOOGLE_ADDITIONAL_LINK, description = ApiDescription.GOOGLE_ADDITIONAL_LINK)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @PostMapping("/me/oauth/google")
    public CommonResponse<Void> googleAdditionalLink(@RequestBody @Valid OauthLoginRequest request) {
        memberService.oauthAdditionalLink(Provider.GOOGLE, request.toServiceRequest());
        return CommonResponse.success(SUCCESS_GOOGLE_LINK, OK);
    }

    @Operation(summary = GET_MY_PROFILE, description = ApiDescription.GET_MY_PROFILE)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @GetMapping("/me")
    public CommonResponse<MyProfileResponse> getMyProfile() {
        MyProfileServiceResponse response = memberService.getMyProfile();
        return CommonResponse.success(MyProfileResponse.from(response));
    }

    @Operation(summary = UPDATE_MY_PROFILE_MAIN, description = ApiDescription.UPDATE_MY_PROFILE_MAIN)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @PatchMapping("/me/main")
    public CommonResponse<Void> updateMyProfileMain(@RequestBody @Valid MyProfileMainUpdateRequest request) {
        RiotAccount riotAccount = memberService.updateMyProfileMain(request.toServiceRequest());
        if (request.getStatus() != null) {
            stompService.updateConnStatus(userService.getUser(MemberThreadLocal.get().getId()), request.getStatus(),
                true);
        }
        if (riotAccount != null) {
            stompService.createOrUpdateUser(riotAccount);
        }
        return CommonResponse.success(SUCCESS_UPDATE_PROFILE, OK);
    }

    @Operation(summary = SEND_PASSWORD_EMAIL_AUTH_CODE, description = ApiDescription.SEND_PASSWORD_EMAIL_AUTH_CODE)
    @ResponseStatus(ACCEPTED)
    @PostMapping("/password/email")
    public CommonResponse<Void> sendEmailAuthCode(@RequestBody @Valid SendEmailRequest request) {
        memberService.sendEmailAuthCode(request.toServiceRequest());
        return CommonResponse.success(SUCCESS_SEND_EMAIL_AUTH_CODE, ACCEPTED);
    }

    @Operation(summary = VERIFY_PASSWORD_EMAIL_AUTH_CODE, description = ApiDescription.VERIFY_PASSWORD_EMAIL_AUTH_CODE)
    @PostMapping("/password/email/code")
    public CommonResponse<PasswordEmailVerifyResponse> verifyEmailAuthCode(
        @RequestBody @Valid PasswordEmailVerifyRequest request
    ) {
        PasswordEmailVerifyServiceResponse response = memberService.verifyEmailAuthCode(request.toServiceRequest());
        return CommonResponse.success(PasswordEmailVerifyResponse.from(response));
    }

    @Operation(summary = RESET_PASSWORD, description = ApiDescription.RESET_PASSWORD)
    @PatchMapping("/password")
    public CommonResponse<Void> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        memberService.resetPassword(request.toServiceRequest());
        return CommonResponse.success(SUCCESS_UPDATE_PASSWORD, OK);
    }

    @Operation(summary = UPDATE_PASSWORD, description = ApiDescription.UPDATE_PASSWORD)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @PatchMapping("/me/password")
    public CommonResponse<Void> updatePassword(@RequestBody @Valid PasswordUpdateRequest request) {
        memberService.updatePassword(request.toServiceRequest());
        return CommonResponse.success(SUCCESS_UPDATE_PASSWORD, OK);
    }

    @Operation(summary = UPDATE_MY_PROFILE, description = ApiDescription.UPDATE_MY_PROFILE)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @PatchMapping("/me")
    public CommonResponse<Void> updateMyProfile(@RequestBody @Valid MyProfileUpdateRequest request) {
        memberService.updateMyProfile(request.toServiceRequest());
        stompService.updateConnStatus(userService.getUser(MemberThreadLocal.get().getId()), request.getStatus(), true);
        return CommonResponse.success(SUCCESS_UPDATE_PROFILE, OK);
    }

    @Operation(summary = DELETE_OAUTH_INFO, description = ApiDescription.DELETE_OAUTH_INFO)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @DeleteMapping("/me/oauth")
    public CommonResponse<Void> deleteOauthInfo(
        @Schema(example = "KAKAO", description = "서비스 제공자") @RequestParam Provider provider
    ) {
        memberService.deleteOauthInfo(provider);
        return CommonResponse.success(SUCCESS_DISCONNECT_OAUTH, OK);
    }

    @Operation(summary = LOGOUT, description = ApiDescription.LOGOUT)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @DeleteMapping("/me/logout")
    public CommonResponse<Void> logout() {
        memberService.logout();
        return CommonResponse.success(SUCCESS_LOGOUT, OK);
    }

    @Operation(summary = WITHDRAWAL, description = ApiDescription.WITHDRAWAL)
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
    @DeleteMapping("/me")
    public CommonResponse<Void> withdrawal() {
        Member member = MemberThreadLocal.get();
        memberService.withdrawal();
        stompService.withdrawal(member.getId());
        return CommonResponse.success(SUCCESS_WITHDRAWAL, OK);
    }

    @Operation(summary = GET_OTHER_PROFILE, description = ApiDescription.GET_OTHER_PROFILE)
    @GetMapping("/{member_id}")
    public CommonResponse<OtherProfileResponse> getOtherProfile(
        @Schema(example = "1", description = "조회할 회원 id") @PathVariable("member_id") Long memberId
    ) {
        OtherProfileServiceResponse response = memberReadService.findOtherById(memberId);
        return CommonResponse.success(OtherProfileResponse.from(response));
    }
}
