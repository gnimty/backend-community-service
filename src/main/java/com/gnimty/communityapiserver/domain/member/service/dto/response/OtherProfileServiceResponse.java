package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OtherProfileServiceResponse {

	private List<ScheduleEntry> schedules;
	private String mainIntroduction;
	private List<PreferGameModeEntry> preferGameModes;
}
