package com.gnimty.communityapiserver.domain.championcomments.service;

import static com.gnimty.communityapiserver.domain.riotaccount.service.utils.ChampionInfoUtil.validateChampionId;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsQueryRepository;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsRepository;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsEntry;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.response.ChampionCommentsServiceResponse;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.List;
import javax.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Lock(LockModeType.OPTIMISTIC)
	public ChampionComments findByIdOptimistic(Long id) {
		return championCommentsRepository.findById(id)
			.orElseThrow(() -> new BaseException(ErrorCode.CHAMPION_COMMENTS_NOT_FOUND));
	}

	public ChampionCommentsServiceResponse findByChampionId(Long championId) {
		validateChampionId(championId);
		List<ChampionCommentsEntry> contents = championCommentsQueryRepository.findByChampionId(championId);
		return ChampionCommentsServiceResponse.builder()
			.championComments(contents)
			.build();
	}
}
