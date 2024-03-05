package com.gnimty.communityapiserver.domain.introduction.repository;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntroductionRepository extends JpaRepository<Introduction, Long> {

	List<Introduction> findByMember(Member member);

	Long countByMemberAndIsMain(Member member, Boolean isMain);

	@Query("update Introduction i set i.deleted = 1, i.updatedAt = :updatedAt where i.member.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}
