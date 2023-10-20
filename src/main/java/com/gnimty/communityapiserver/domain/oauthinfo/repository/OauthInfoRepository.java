package com.gnimty.communityapiserver.domain.oauthinfo.repository;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.oauthinfo.entity.OauthInfo;
import com.gnimty.communityapiserver.global.constant.Provider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OauthInfoRepository extends JpaRepository<OauthInfo, Long> {

	Optional<OauthInfo> findByEmail(String email);

	Boolean existsByEmailAndProvider(String email, Provider provider);

	List<OauthInfo> findByMember(Member member);

	Boolean existsByMemberAndProvider(Member member, Provider provider);

	Optional<OauthInfo> findByMemberAndProvider(Member member, Provider provider);

	@Query("delete from OauthInfo o where o.member.id = :id")
	@Modifying
	void deleteAllFromMember(@Param("id") Long id);
}
