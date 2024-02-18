package com.gnimty.communityapiserver.domain.schedule.service.dto.request;

import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ScheduleServiceRequest {

    private List<ScheduleEntry> schedules;
}
