package com.gnimty.communityapiserver.domain.oauthinfo.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.oauthinfo.entity.OauthInfo;
import com.gnimty.communityapiserver.domain.oauthinfo.repository.OauthInfoRepository;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OauthInfoReadService {

	private final OauthInfoRepository oauthInfoRepository;

	public void throwIfExistsByEmailAndProvider(String email, Provider provider) {
		if (oauthInfoRepository.existsByEmailAndProvider(email, provider)) {
			throw new BaseException(ErrorCode.ALREADY_LINKED_OAUTH);
		}
	}

	public List<OauthInfo> findByMember(Member member) {
		return oauthInfoRepository.findByMember(member);
	}

	public Boolean existsByMemberAndProvider(Member member, Provider provider) {
		return oauthInfoRepository.existsByMemberAndProvider(member, provider);
	}

	public OauthInfo findByMemberAndProvider(Member member, Provider provider) {
		return oauthInfoRepository.findByMemberAndProvider(member, provider)
			.orElseThrow(() -> new BaseException(ErrorCode.OAUTH_INFO_NOT_FOUND));
	}
}
