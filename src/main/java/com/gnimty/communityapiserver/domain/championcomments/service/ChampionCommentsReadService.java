package com.gnimty.communityapiserver.domain.championcomments.service;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsQueryRepository;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsRepository;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsEntry;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsServiceResponse;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChampionCommentsReadService {

    private final ChampionCommentsRepository championCommentsRepository;
    private final ChampionCommentsQueryRepository championCommentsQueryRepository;

    public ChampionComments findById(Long id) {
        return championCommentsRepository.findById(id)
            .orElseThrow(() -> new BaseException(ErrorCode.CHAMPION_COMMENTS_NOT_FOUND));
    }

    public ChampionCommentsServiceResponse findByChampionId(Long championId) {
        List<ChampionCommentsEntry> contents = championCommentsQueryRepository.findByChampionId(championId);
        return ChampionCommentsServiceResponse.builder()
            .championComments(contents)
            .build();
    }
}
