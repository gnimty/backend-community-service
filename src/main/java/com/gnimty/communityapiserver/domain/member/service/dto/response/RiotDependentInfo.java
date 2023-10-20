package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.global.constant.Status;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RiotDependentInfo {

	private Boolean isLinked;
	private Status status;
	private List<IntroductionEntry> introductions;
	private List<ScheduleEntry> schedules;
	private List<PreferGameModeEntry> preferGameModes;
	private List<RiotAccountEntry> riotAccounts;
}
