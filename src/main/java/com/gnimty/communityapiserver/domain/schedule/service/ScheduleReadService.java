package com.gnimty.communityapiserver.domain.schedule.service;

import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.domain.schedule.repository.ScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ScheduleReadService {

	private final ScheduleRepository scheduleRepository;

	public List<Schedule> findByMember(Member member) {
		return scheduleRepository.findByMember(member);
	}
}
