package com.gnimty.communityapiserver.domain.member.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberQueryRepository;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OtherProfileServiceResponse;
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
	private final MemberQueryRepository memberQueryRepository;

	public void throwIfExistByEmail(String email) {
		if (memberQueryRepository.existsByEmail(email)) {
			throw new BaseException(ErrorCode.ALREADY_REGISTERED_EMAIL);
		}
	}

	public Member findByEmailOrElseThrow(String email, BaseException exception) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> exception);
	}

	public Member findById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
	}

	public Boolean existsById(Long id) {
		return memberQueryRepository.existsById(id);
	}

	public OtherProfileServiceResponse findOtherById(Long id) {
		return memberQueryRepository.findOtherById(id);
	}

	public Long findUpCountByPuuid(String puuid) {
		return memberQueryRepository.findUpCountByPuuid(puuid);
	}
}
