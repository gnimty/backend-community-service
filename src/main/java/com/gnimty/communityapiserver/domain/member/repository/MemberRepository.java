package com.gnimty.communityapiserver.domain.member.repository;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	@Query("update Member m set m.deleted = 1, m.updatedAt = :updatedAt where m.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}
