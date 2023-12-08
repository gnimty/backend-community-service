package com.gnimty.communityapiserver.domain.schedule.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.domain.schedule.repository.ScheduleRepository;
import com.gnimty.communityapiserver.domain.schedule.service.dto.request.ScheduleServiceRequest;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public void updateSchedules(ScheduleServiceRequest request) {
        Member member = MemberThreadLocal.get();
        scheduleRepository.deleteByMember(member);

        request.getSchedules().stream()
            .map(entry -> createSchedule(entry, member))
            .forEach(scheduleRepository::save);
    }

    public Schedule createSchedule(ScheduleEntry entry, Member member) {
        return Schedule.builder()
            .dayOfWeek(entry.getDayOfWeek())
            .startTime(entry.getStartTime())
            .endTime(entry.getEndTime())
            .member(member)
            .build();
    }
}
