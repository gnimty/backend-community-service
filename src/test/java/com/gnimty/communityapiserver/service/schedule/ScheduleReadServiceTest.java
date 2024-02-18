package com.gnimty.communityapiserver.service.schedule;

import static org.assertj.core.api.Assertions.assertThat;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.domain.schedule.repository.ScheduleRepository;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleReadService;
import com.gnimty.communityapiserver.global.constant.DayOfWeek;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ScheduleReadServiceTest extends ServiceTestSupport {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleReadService scheduleReadService;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원으로 조회 시")
    @Nested
    class FindByMember {

        private Member member;

        @BeforeEach
        void setUp() {
            member = memberRepository.save(Member.builder()
                .upCount(0L)
                .status(Status.ONLINE)
                .nickname("nickname")
                .rsoLinked(true)
                .build());
        }

        @DisplayName("존재하는 모든 일정이 조회된다.")
        @Test
        @Transactional
        void should_readAllSchedules_when_invokeMethod() {
            List<Schedule> saved = scheduleRepository.saveAll(List.of(
                createSchedule(DayOfWeek.SUNDAY),
                createSchedule(DayOfWeek.MONDAY),
                createSchedule(DayOfWeek.TUESDAY),
                createSchedule(DayOfWeek.WEDNESDAY),
                createSchedule(DayOfWeek.THURSDAY),
                createSchedule(DayOfWeek.FRIDAY),
                createSchedule(DayOfWeek.SATURDAY)
            ));

            List<Schedule> find = scheduleReadService.findByMember(member);

            assertThat(find).hasSize(saved.size());
            assertThat(find).containsAll(saved);
        }

        private Schedule createSchedule(DayOfWeek dayOfWeek) {
            return Schedule.builder()
                .member(member)
                .dayOfWeek(dayOfWeek)
                .startTime(0)
                .endTime(24)
                .build();
        }
    }
}
