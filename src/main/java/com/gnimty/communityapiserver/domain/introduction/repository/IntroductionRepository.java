package com.gnimty.communityapiserver.domain.introduction.repository;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IntroductionRepository extends JpaRepository<Introduction, Long> {

    List<Introduction> findByMember(Member member);

    Long countByMemberAndIsMain(Member member, Boolean isMain);

    @Query("delete from Introduction i where i.member.id = :id")
    @Modifying
    void deleteAllFromMember(@Param("id") Long id);
}
