package com.gnimty.communityapiserver.domain.memberlike.controller;

import static com.gnimty.communityapiserver.global.constant.ApiSummary.DO_MEMBER_LIKE;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.READ_MEMBER_LIKE;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_MEMBER_LIKE;
import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.memberlike.controller.dto.request.MemberLikeRequest;
import com.gnimty.communityapiserver.domain.memberlike.controller.dto.response.MemberLikeResponse;
import com.gnimty.communityapiserver.domain.memberlike.service.MemberLikeService;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.ApiDescription;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/members/me/like", description = "회원 좋아요 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/me/like")
public class MemberLikeController {

	private final MemberLikeService memberLikeService;

	@Operation(summary = READ_MEMBER_LIKE, description = ApiDescription.READ_MEMBER_LIKE)
	@Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
	@GetMapping
	public CommonResponse<MemberLikeResponse> readMemberLike() {
		MemberLikeResponse response = MemberLikeResponse.builder()
			.upCount(MemberThreadLocal.get().getUpCount())
			.build();
		return CommonResponse.success(response);
	}

	@Operation(summary = DO_MEMBER_LIKE, description = ApiDescription.DO_MEMBER_LIKE)
	@Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
	@PostMapping
	public CommonResponse<Void> doMemberLike(@RequestBody @Valid MemberLikeRequest request) {
		memberLikeService.doMemberLike(request.toServiceRequest());
		return CommonResponse.success(SUCCESS_MEMBER_LIKE, OK);
	}
}
