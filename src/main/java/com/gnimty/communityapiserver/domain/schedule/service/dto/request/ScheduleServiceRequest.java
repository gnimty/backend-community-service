package com.gnimty.communityapiserver.domain.schedule.service.dto.request;

import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduleServiceRequest {

	private List<ScheduleEntry> schedules;
}
