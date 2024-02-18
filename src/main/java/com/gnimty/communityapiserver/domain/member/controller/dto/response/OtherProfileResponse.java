package com.gnimty.communityapiserver.domain.member.controller.dto.response;

import com.gnimty.communityapiserver.domain.member.service.dto.response.OtherProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OtherProfileResponse {

    @Schema(description = "선호 게임 시간 정보")
    private List<ScheduleEntry> schedules;
    @Schema(example = "intro", description = "대표 소개글")
    private String mainIntroduction;
    @Schema(description = "선호 게임 모드 정보")
    private List<PreferGameModeEntry> preferGameModes;

    public static OtherProfileResponse from(OtherProfileServiceResponse response) {
        return OtherProfileResponse.builder()
            .schedules(response.getSchedules())
            .mainIntroduction(response.getMainIntroduction())
            .preferGameModes(response.getPreferGameModes())
            .build();
    }
}
