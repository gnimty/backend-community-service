package com.gnimty.communityapiserver.domain.championcommentslike.repository;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcommentslike.entity.ChampionCommentsLike;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChampionCommentsLikeRepository extends JpaRepository<ChampionCommentsLike, Long> {

    Optional<ChampionCommentsLike> findByMemberAndChampionComments(Member member, ChampionComments championComments);
}
