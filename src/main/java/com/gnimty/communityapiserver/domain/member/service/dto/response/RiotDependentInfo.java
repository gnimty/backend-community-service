package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.global.constant.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RiotDependentInfo {

	@Schema(example = "true", description = "연동 여부, false인 경우 이후 모든 정보는 null")
	private Boolean isLinked;
	@Schema(example = "OFFLINE", description = "상태")
	private Status status;
	@Schema(description = "소개글 정보")
	private List<IntroductionEntry> introductions;
	@Schema(description = "플레이 가능 시간대 정보")
	private List<ScheduleEntry> schedules;
	@Schema(description = "선호 게임 모드 정보")
	private List<PreferGameModeEntry> preferGameModes;
	@Schema(description = "rso 연동 정보")
	private List<RiotAccountEntry> riotAccounts;
}
