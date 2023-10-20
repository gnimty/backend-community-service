package com.gnimty.communityapiserver.domain.memberlike.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.memberlike.entity.MemberLike;
import com.gnimty.communityapiserver.domain.memberlike.repository.MemberLikeRepository;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberLikeReadService {

	private final MemberLikeRepository memberLikeRepository;

	public MemberLike findBySourceAndTarget(Member source, Member target) {
		return memberLikeRepository.findBySourceMemberAndTargetMember(source, target)
			.orElseThrow(() -> new BaseException(ErrorCode.MEMBER_LIKE_NOT_FOUND));
	}

	public Boolean existsBySourceAndTarget(Member source, Member target) {
		return memberLikeRepository.existsBySourceMemberAndTargetMember(source, target);
	}

	public List<MemberLike> findBySourceMember(Member source) {
		return memberLikeRepository.findBySourceMember(source);
	}
}
