package com.gnimty.communityapiserver.domain.riotaccount.repository;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RiotAccountRepository extends JpaRepository<RiotAccount, Long> {

	List<RiotAccount> findByMember(Member member);

	Optional<RiotAccount> findByMemberAndIsMain(Member member, Boolean isMain);

	@Query("SELECT ra FROM RiotAccount ra WHERE ra.puuid IN :puuids")
	List<RiotAccount> findByPuuids(@Param("puuids") List<String> puuids);

	@Query("update RiotAccount r set r.deleted = 1, r.updatedAt = :updatedAt where r.member.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);

	Optional<RiotAccount> findByMemberIdAndIsMain(Long memberId, Boolean isMain);
}
