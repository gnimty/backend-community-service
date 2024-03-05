package com.gnimty.communityapiserver.domain.championcomments.repository;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChampionCommentsRepository extends JpaRepository<ChampionComments, Long> {

	@Query("update ChampionComments c set c.deleted = 1, c.member.id = null, c.updatedAt = :updatedAt where c.member.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}
