package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.global.constant.Status;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyProfileUpdateServiceRequest {

    private Status status;
    private List<IntroductionEntry> introductions;
    private List<PreferGameModeEntry> preferGameModes;
    private List<ScheduleEntry> schedules;
}
