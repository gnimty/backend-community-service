package com.gnimty.communityapiserver.domain.championcommentsreport.reposiotry;

import com.gnimty.communityapiserver.domain.championcommentsreport.entity.ChampionCommentsReport;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChampionCommentsReportRepository extends JpaRepository<ChampionCommentsReport, Long> {

	@Query("update ChampionCommentsReport cr set cr.deleted = 1, cr.updatedAt = :updatedAt where cr.member.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}
