package com.gnimty.communityapiserver.domain.championcommentslike.repository;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcommentslike.entity.ChampionCommentsLike;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChampionCommentsLikeRepository extends JpaRepository<ChampionCommentsLike, Long> {

	Optional<ChampionCommentsLike> findByMemberAndChampionComments(Member member, ChampionComments championComments);

	@Query("update ChampionCommentsLike cl set cl.deleted = 1 where cl.member.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id);

	@Query("select cl.championComments.id from ChampionCommentsLike cl where cl.member.id = :id and cl.likeOrNot = :likeOrNot")
	List<Long> findByMemberIdAndLikeOrNot(@Param("id") Long id, @Param("likeOrNot") Boolean likeOrNot);
}
