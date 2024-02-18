package com.gnimty.communityapiserver.domain.championcommentslike.service;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.service.ChampionCommentsReadService;
import com.gnimty.communityapiserver.domain.championcommentslike.entity.ChampionCommentsLike;
import com.gnimty.communityapiserver.domain.championcommentslike.repository.ChampionCommentsLikeRepository;
import com.gnimty.communityapiserver.domain.championcommentslike.service.dto.request.ChampionCommentsLikeServiceRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ChampionCommentsLikeService {

    private final ChampionCommentsLikeRepository championCommentsLikeRepository;
    private final ChampionCommentsLikeReadService championCommentsLikeReadService;
    private final ChampionCommentsReadService championCommentsReadService;

    public void doChampionCommentsLike(Long championId, Long commentsId, ChampionCommentsLikeServiceRequest request) {
        Member member = MemberThreadLocal.get();

        if (!member.getRsoLinked()) {
            throw new BaseException(NOT_LINKED_RSO);
        }
        ChampionComments championComments = championCommentsReadService.findById(commentsId);
        checkValidation(championId, championComments);

        if (!request.getCancel()) {
            championCommentsLike(request, member, championComments);
        } else {
            cancelChampionCommentsLike(member, championComments);
        }
    }

    private void cancelChampionCommentsLike(Member member, ChampionComments championComments) {
        ChampionCommentsLike championCommentsLike = championCommentsLikeReadService
            .findByMemberAndChampionComments(member, championComments);
        championCommentsLikeRepository.delete(championCommentsLike);
    }

    private void championCommentsLike(
        ChampionCommentsLikeServiceRequest request,
        Member member,
        ChampionComments championComments
    ) {
        if (championCommentsLikeReadService.existsByMemberAndChampionComments(member, championComments)) {
            throw new BaseException(ALREADY_CHAMPION_COMMENTS_LIKE);
        }
        championCommentsLikeRepository.save(createChampionCommentsLike(request, member, championComments));
        updateReactionCount(request.getLikeOrNot(), championComments);
    }

    private void updateReactionCount(Boolean likeOrNot, ChampionComments championComments) {
        if (likeOrNot) {
            championComments.increaseUpCount();
        } else {
            championComments.increaseDownCount();
        }
    }

    private ChampionCommentsLike createChampionCommentsLike(
        ChampionCommentsLikeServiceRequest request,
        Member member,
        ChampionComments championComments
    ) {
        return ChampionCommentsLike.builder()
            .likeOrNot(request.getLikeOrNot())
            .member(member)
            .championComments(championComments)
            .build();
    }

    private void checkValidation(Long championId, ChampionComments championComments) {
        if (!Objects.equals(championComments.getChampionId(), championId)) {
            throw new BaseException(COMMENTS_ID_AND_CHAMPION_ID_INVALID);
        }
    }
}