package com.gnimty.communityapiserver.domain.championcommentslike.service;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcommentslike.entity.ChampionCommentsLike;
import com.gnimty.communityapiserver.domain.championcommentslike.repository.ChampionCommentsLikeQueryRepository;
import com.gnimty.communityapiserver.domain.championcommentslike.repository.ChampionCommentsLikeRepository;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChampionCommentsLikeReadService {

    private final ChampionCommentsLikeQueryRepository championCommentsLikeQueryRepository;
    private final ChampionCommentsLikeRepository championCommentsLikeRepository;

    public Boolean existsByMemberAndChampionComments(
        Member member,
        ChampionComments championComments
    ) {
        return championCommentsLikeQueryRepository.existsByMemberAndChampionComments(
            member, championComments);
    }

    public ChampionCommentsLike findByMemberAndChampionComments(
        Member member,
        ChampionComments championComments
    ) {
        return championCommentsLikeRepository.findByMemberAndChampionComments(
                member, championComments)
            .orElseThrow(() -> new BaseException(ErrorCode.CHAMPION_COMMENTS_LIKE_NOT_FOUND));
    }
}
