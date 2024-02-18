package com.gnimty.communityapiserver.domain.memberlike.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.memberlike.entity.MemberLike;
import com.gnimty.communityapiserver.domain.memberlike.repository.MemberLikeQueryRepository;
import com.gnimty.communityapiserver.domain.memberlike.repository.MemberLikeRepository;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberLikeReadService {

    private final MemberLikeRepository memberLikeRepository;
    private final MemberLikeQueryRepository memberLikeQueryRepository;

    public MemberLike findBySourceAndTarget(Member source, Member target) {
        return memberLikeRepository.findBySourceMemberAndTargetMember(source, target)
            .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_LIKE_NOT_FOUND));
    }

    public Boolean existsBySourceAndTarget(Member source, Member target) {
        return memberLikeQueryRepository.existsBySourceMemberAndTargetMember(source, target);
    }

    public List<MemberLike> findBySourceMember(Member source) {
        return memberLikeRepository.findBySourceMember(source);
    }
}
