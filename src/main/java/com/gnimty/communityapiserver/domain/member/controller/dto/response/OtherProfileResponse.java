package com.gnimty.communityapiserver.domain.member.controller.dto.response;

import com.gnimty.communityapiserver.domain.member.service.dto.response.OtherProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OtherProfileResponse {

    private List<ScheduleEntry> schedules;
    private String mainIntroduction;
    private List<PreferGameModeEntry> preferGameModes;

    public static OtherProfileResponse from(OtherProfileServiceResponse response) {
        return OtherProfileResponse.builder()
            .schedules(response.getSchedules())
            .mainIntroduction(response.getMainIntroduction())
            .preferGameModes(response.getPreferGameModes())
            .build();
    }
}
