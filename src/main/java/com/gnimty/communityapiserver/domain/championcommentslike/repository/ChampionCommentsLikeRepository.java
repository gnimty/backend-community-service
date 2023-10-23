package com.gnimty.communityapiserver.domain.championcommentslike.repository;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcommentslike.entity.ChampionCommentsLike;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionCommentsLikeRepository extends JpaRepository<ChampionCommentsLike, Long> {

	Boolean existsByMemberAndChampionComments(Member member, ChampionComments championComments);
	Optional<ChampionCommentsLike> findByMemberAndChampionComments(Member member, ChampionComments championComments);
}
