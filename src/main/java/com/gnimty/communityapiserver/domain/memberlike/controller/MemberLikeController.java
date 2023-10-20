package com.gnimty.communityapiserver.domain.memberlike.controller;

import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_MEMBER_LIKE;
import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.memberlike.controller.dto.request.MemberLikeRequest;
import com.gnimty.communityapiserver.domain.memberlike.controller.dto.response.MemberLikeResponse;
import com.gnimty.communityapiserver.domain.memberlike.service.MemberLikeService;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{member_id}/like")
public class MemberLikeController {

	private final MemberLikeService memberLikeService;

	@GetMapping
	public CommonResponse<MemberLikeResponse> readMemberLike() {
		MemberLikeResponse response = MemberLikeResponse.builder()
			.upCount(MemberThreadLocal.get().getUpCount())
			.build();
		return CommonResponse.success(response);
	}

	@PostMapping
	public CommonResponse<Void> doMemberLike(@RequestBody @Valid MemberLikeRequest request) {
		memberLikeService.doMemberLike(request.toServiceRequest());
		return CommonResponse.success(SUCCESS_MEMBER_LIKE, OK);
	}
}
