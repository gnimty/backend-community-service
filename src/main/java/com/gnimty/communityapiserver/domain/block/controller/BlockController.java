package com.gnimty.communityapiserver.domain.block.controller;

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
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/me/block")
public class BlockController {

	private final BlockReadService blockReadService;
	private final BlockService blockService;
	private final StompService stompService;

	@GetMapping
	public CommonResponse<BlockReadResponse> readBlocks() {
		BlockReadServiceResponse response = blockReadService.readBlocks();
		return CommonResponse.success(BlockReadResponse.from(response));
	}

	@PostMapping
	public CommonResponse<Void> doBlock(@RequestBody @Valid BlockRequest request) {
		Member member = MemberThreadLocal.get();
		blockService.doBlock(member, request.toServiceRequest());
		stompService.updateBlockStatus(member.getId(), request.getId(), Blocked.BLOCK);
		return CommonResponse.success(SUCCESS_BLOCK, OK);
	}

	@DeleteMapping
	public CommonResponse<Void> clearBlock(@RequestBody @Valid BlockClearRequest request) {
		Member member = MemberThreadLocal.get();
		blockService.clearBlock(member, request.toServiceRequest());
		stompService.updateBlockStatus(member.getId(), request.getId(), Blocked.UNBLOCK);
		return CommonResponse.success(SUCCESS_CLEAR_BLOCK, OK);
	}
}
