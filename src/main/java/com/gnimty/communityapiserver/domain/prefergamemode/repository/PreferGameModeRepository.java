package com.gnimty.communityapiserver.domain.prefergamemode.repository;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PreferGameModeRepository extends JpaRepository<PreferGameMode, Long> {

	List<PreferGameMode> findByMember(Member member);

	void deleteByMember(Member member);

	@Query("update PreferGameMode p set p.deleted = 1, p.updatedAt = :updatedAt where p.member.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}
