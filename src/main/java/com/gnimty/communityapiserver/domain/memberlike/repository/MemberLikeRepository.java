package com.gnimty.communityapiserver.domain.memberlike.repository;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.memberlike.entity.MemberLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberLikeRepository extends JpaRepository<MemberLike, Long> {

	Optional<MemberLike> findBySourceMemberAndTargetMember(Member sourceMember,
		Member targetMember);

	List<MemberLike> findBySourceMember(Member sourceMember);

	@Query("delete from MemberLike ml where ml.sourceMember.id = :id or ml.targetMember.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id);
}
