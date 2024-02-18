package com.gnimty.communityapiserver.domain.introduction.service;

import static com.gnimty.communityapiserver.global.constant.Bound.MAIN_CONTENT_COUNT;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.introduction.repository.IntroductionRepository;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IntroductionReadService {

    private final IntroductionRepository introductionRepository;

    public List<Introduction> findByMember(Member member) {
        return introductionRepository.findByMember(member);
    }

    public Introduction findById(Long id) {
        return introductionRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.INTRODUCTION_NOT_FOUND));
    }

    public void throwIfExceedMain(Member member) {
        Long count = introductionRepository.countByMemberAndIsMain(member, true);
        if (count > MAIN_CONTENT_COUNT.getValue()) {
            throw new BaseException(ErrorCode.MAIN_CONTENT_MUST_BE_ONLY);
        }
    }
}
