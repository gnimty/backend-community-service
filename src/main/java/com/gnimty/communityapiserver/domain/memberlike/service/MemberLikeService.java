package com.gnimty.communityapiserver.domain.memberlike.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.domain.memberlike.entity.MemberLike;
import com.gnimty.communityapiserver.domain.memberlike.repository.MemberLikeRepository;
import com.gnimty.communityapiserver.domain.memberlike.service.dto.request.MemberLikeServiceRequest;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberLikeService {

    private final MemberLikeRepository memberLikeRepository;
    private final MemberLikeReadService memberLikeReadService;
    private final MemberReadService memberReadService;

    public void doMemberLike(MemberLikeServiceRequest request) {
        Member source = MemberThreadLocal.get();
        Member target = memberReadService.findById(request.getTargetMemberId());

        if (request.getCancel()) {
            cancelMemberLike(source, target);
        } else {
            memberLike(source, target);
        }
    }

    private void memberLike(Member source, Member target) {
        if (Objects.equals(source.getId(), target.getId())) {
            throw new BaseException(ErrorCode.NOT_ALLOWED_SELF_LIKE);
        }
        if (memberLikeReadService.existsBySourceAndTarget(source, target)) {
            throw new BaseException(ErrorCode.ALREADY_MEMBER_LIKE);
        }
        target.increaseUpCount();
        memberLikeRepository.save(MemberLike.builder()
            .sourceMember(source)
            .targetMember(target)
            .build()
        );
    }

    private void cancelMemberLike(Member source, Member target) {
        MemberLike memberLike = memberLikeReadService.findBySourceAndTarget(source, target);
        memberLikeRepository.delete(memberLike);
    }
}
