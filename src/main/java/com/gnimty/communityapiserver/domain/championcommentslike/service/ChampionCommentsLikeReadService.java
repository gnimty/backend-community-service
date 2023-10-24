package com.gnimty.communityapiserver.domain.championcommentslike.service;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcommentslike.repository.ChampionCommentsLikeQueryRepository;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChampionCommentsLikeReadService {

	private final ChampionCommentsLikeQueryRepository championCommentsLikeQueryRepository;

	public Boolean existsByMemberAndChampionComments(
		Member member,
		ChampionComments championComments
	) {
		return championCommentsLikeQueryRepository.existsByMemberAndChampionComments(
			member, championComments);
	}
}
