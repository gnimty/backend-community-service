package com.gnimty.communityapiserver.domain.member.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberReadService {

	private final MemberRepository memberRepository;

	public void throwIfExistByEmail(String email) {
		if (memberRepository.existsByEmail(email)) {
			throw new BaseException(ErrorCode.ALREADY_REGISTERED_EMAIL);
		}
	}

	public Member findByEmailOrElseThrow(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new BaseException(ErrorCode.INVALID_LOGIN));
	}

	public Member findById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public Boolean existsById(Long id) {
		return memberRepository.existsById(id);
	}
}
