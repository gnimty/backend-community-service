package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OtherProfileServiceResponse {

    private List<ScheduleEntry> schedules;
    private String mainIntroduction;
    private List<PreferGameModeEntry> preferGameModes;
}
