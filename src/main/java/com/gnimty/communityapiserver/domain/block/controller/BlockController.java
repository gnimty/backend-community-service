package com.gnimty.communityapiserver.domain.block.controller;

import static com.gnimty.communityapiserver.global.constant.ApiSummary.CLEAR_BLOCK;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.DO_BLOCK;
import static com.gnimty.communityapiserver.global.constant.ApiSummary.READ_BLOCKS;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_BLOCK;
import static com.gnimty.communityapiserver.global.constant.ResponseMessage.SUCCESS_CLEAR_BLOCK;
import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.block.controller.dto.request.BlockClearRequest;
import com.gnimty.communityapiserver.domain.block.controller.dto.request.BlockRequest;
import com.gnimty.communityapiserver.domain.block.controller.dto.response.BlockReadResponse;
import com.gnimty.communityapiserver.domain.block.service.BlockReadService;
import com.gnimty.communityapiserver.domain.block.service.BlockService;
import com.gnimty.communityapiserver.domain.block.service.dto.response.BlockReadServiceResponse;
import com.gnimty.communityapiserver.domain.chat.entity.Blocked;
import com.gnimty.communityapiserver.domain.chat.service.StompService;
import com.gnimty.communityapiserver.domain.chat.service.UserService;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.ApiDescription;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "/members/me/block", description = "회원 차단 관련 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members/me/block")
public class BlockController {

	private final BlockReadService blockReadService;
	private final BlockService blockService;
	private final StompService stompService;
	private final UserService userService;

	@Operation(summary = READ_BLOCKS, description = ApiDescription.READ_BLOCKS)
	@Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
	@GetMapping
	public CommonResponse<BlockReadResponse> readBlocks() {
		BlockReadServiceResponse response = blockReadService.readBlocks();
		return CommonResponse.success(BlockReadResponse.from(response));
	}

	@Operation(summary = DO_BLOCK, description = ApiDescription.DO_BLOCK)
	@Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
	@PostMapping
	public CommonResponse<Void> doBlock(@RequestBody @Valid BlockRequest request) {
		Member member = MemberThreadLocal.get();
		blockService.doBlock(member, request.toServiceRequest());
		stompService.updateBlockStatus(userService.getUser(member.getId()),
			userService.getUser(request.getId()), Blocked.BLOCK);
		return CommonResponse.success(SUCCESS_BLOCK, OK);
	}

	@Operation(summary = CLEAR_BLOCK, description = ApiDescription.CLEAR_BLOCK)
	@Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "인증을 위한 Access Token", required = true)
	@DeleteMapping
	public CommonResponse<Void> clearBlock(@RequestBody @Valid BlockClearRequest request) {
		Member member = MemberThreadLocal.get();
		blockService.clearBlock(member, request.toServiceRequest());
		stompService.updateBlockStatus(userService.getUser(member.getId()),
			userService.getUser(request.getId()), Blocked.UNBLOCK);
		return CommonResponse.success(SUCCESS_CLEAR_BLOCK, OK);
	}
}
