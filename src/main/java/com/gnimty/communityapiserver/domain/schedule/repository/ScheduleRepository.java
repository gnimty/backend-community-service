package com.gnimty.communityapiserver.domain.schedule.repository;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByMember(Member member);

    void deleteByMember(Member member);

    @Query("delete from Schedule s where s.member.id = :id")
    @Modifying
    void deleteAllFromMember(@Param("id") Long id);
}
