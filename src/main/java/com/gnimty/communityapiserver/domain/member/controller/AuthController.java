package com.gnimty.communityapiserver.domain.member.controller;

import static com.gnimty.communityapiserver.global.constant.ApiSummary.GOOGLE_LOGIN;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.KAKAO_LOGIN;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.LOGIN;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.SEND_EMAIL_AUTH_CODE;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.SIGNUP;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.TOKEN_REFRESH;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.VERIFY_EMAIL_AUTH_CODE;
import static com.gnimty.communityapiserver.global.constant.Auth.BEARER;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_SEND_EMAIL_AUTH_CODE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_SIGN_UP;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_VERIFY_EMAIL;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.member.controller.dto.request.EmailAuthRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.EmailVerifyRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.LoginRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.OauthLoginRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.request.SignupRequest;
import com.gnimty.communityapiserver.domain.member.controller.dto.response.AuthToken;
import com.gnimty.communityapiserver.domain.member.service.AuthService;
import com.gnimty.communityapiserver.global.constant.ApiDescription;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@Operation(summary = SIGNUP, description = ApiDescription.SIGNUP)
	@ResponseStatus(CREATED)
	@PostMapping("/auth/signup")
	public CommonResponse<Void> signup(@RequestBody @Valid SignupRequest request) {
		authService.signup(request.toServiceRequest());
		return CommonResponse.success(SUCCESS_SIGN_UP, CREATED);
	}

	@Operation(summary = LOGIN, description = ApiDescription.LOGIN)
	@PostMapping("/auth/login")
	public CommonResponse<AuthToken> login(@RequestBody @Valid LoginRequest request) {
		return CommonResponse.success(authService.login(request.toServiceRequest()));
	}

	@Operation(summary = KAKAO_LOGIN, description = ApiDescription.KAKAO_LOGIN)
	@PostMapping("/oauth/kakao")
	public CommonResponse<AuthToken> kakaoLogin(@RequestBody @Valid OauthLoginRequest request) {
		return CommonResponse.success(authService.kakaoLogin(request.toServiceRequest()));
	}

	@Operation(summary = GOOGLE_LOGIN, description = ApiDescription.GOOGLE_LOGIN)
	@PostMapping("/oauth/google")
	public CommonResponse<AuthToken> googleLogin(@RequestBody @Valid OauthLoginRequest request) {
		return CommonResponse.success(authService.googleLogin(request.toServiceRequest()));
	}

	@Operation(summary = SEND_EMAIL_AUTH_CODE, description = ApiDescription.SEND_EMAIL_AUTH_CODE)
	@ResponseStatus(ACCEPTED)
	@PostMapping("/auth/email")
	public CommonResponse<Void> sendEmailAuthCode(@RequestBody @Valid EmailAuthRequest request) {
		authService.sendEmailAuthCode(request.toServiceRequest());
		return CommonResponse.success(SUCCESS_SEND_EMAIL_AUTH_CODE, ACCEPTED);
	}

	@Operation(summary = VERIFY_EMAIL_AUTH_CODE, description = ApiDescription.VERIFY_EMAIL_AUTH_CODE)
	@PostMapping("/auth/email/code")
	public CommonResponse<Void> verifyEmailAuthCode(@RequestBody @Valid EmailVerifyRequest request) {
		authService.verifyEmailAuthCode(request.toServiceRequest());
		return CommonResponse.success(SUCCESS_VERIFY_EMAIL, OK);
	}

	@Operation(summary = TOKEN_REFRESH, description = ApiDescription.TOKEN_REFRESH)
	@Parameter(in = ParameterIn.HEADER, name = "RefreshToken", description = "token 재발급을 위한 RefreshToken", required = true)
	@GetMapping("/auth/refresh")
	public CommonResponse<AuthToken> tokenRefresh(@RequestHeader("RefreshToken") String refreshToken) {
		return CommonResponse.success(
			authService.tokenRefresh(refreshToken.replaceAll(BEARER.getContent(), "")));
	}
}
