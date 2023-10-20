package com.gnimty.communityapiserver.domain.member.repository;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Boolean existsByEmail(String email);

	Optional<Member> findByEmail(String email);
}
