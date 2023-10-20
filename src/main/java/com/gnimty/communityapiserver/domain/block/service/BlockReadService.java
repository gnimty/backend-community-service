package com.gnimty.communityapiserver.domain.block.service;

import com.gnimty.communityapiserver.domain.block.controller.dto.response.BlockEntry;
import com.gnimty.communityapiserver.domain.block.entity.Block;
import com.gnimty.communityapiserver.domain.block.repository.BlockRepository;
import com.gnimty.communityapiserver.domain.block.service.dto.response.BlockReadServiceResponse;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BlockReadService {

	private final BlockRepository blockRepository;

	public BlockReadServiceResponse readBlocks() {
		Member member = MemberThreadLocal.get();
		List<Block> block = blockRepository.findByBlocker(member);
		return BlockReadServiceResponse.builder()
			.blocks(block.stream()
				.map(BlockEntry::from)
				.toList()
			).build();
	}

	public Boolean existsByBlockerAndBlocked(Member blocker, Member blocked) {
		return blockRepository.existsByBlockerAndBlocked(blocker, blocked);
	}

	public Block findById(Long id) {
		return blockRepository.findById(id)
			.orElseThrow(() -> new BaseException(ErrorCode.BLOCK_NOT_FOUND));
	}
}