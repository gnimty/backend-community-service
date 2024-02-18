package com.gnimty.communityapiserver.domain.schedule.controller.dto.request;

import com.gnimty.communityapiserver.domain.schedule.service.dto.request.ScheduleServiceRequest;
import lombok.Getter;

import javax.validation.Valid;
import java.util.List;

@Getter
public class ScheduleRequest {

    @Valid
    private List<ScheduleEntry> schedules;

    public ScheduleServiceRequest toServiceRequest() {
        return ScheduleServiceRequest.builder()
            .schedules(schedules)
            .build();
    }
}
