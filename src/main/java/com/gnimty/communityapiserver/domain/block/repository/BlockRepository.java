package com.gnimty.communityapiserver.domain.block.repository;

import com.gnimty.communityapiserver.domain.block.entity.Block;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlockRepository extends JpaRepository<Block, Long> {

	List<Block> findByBlocker(Member blocker);

	@Query("update Block b set b.deleted = 1, b.updatedAt = :updatedAt where b.blocker.id = :id or b.blocked.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}
