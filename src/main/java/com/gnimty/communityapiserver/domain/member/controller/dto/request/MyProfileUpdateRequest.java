package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MyProfileUpdateRequest {

	@Schema(example = "OFFLINE", description = "변경할 상태, not null")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Status status;
	@Builder.Default
	@Schema(description = "변경할 소개글 정보")
	private List<@Valid IntroductionEntry> introductions = new ArrayList<>();
	@Builder.Default
	@Schema(description = "변경할 선호 게임 모드 정보")
	private List<@Valid PreferGameModeEntry> preferGameModes = new ArrayList<>();
	@Builder.Default
	@Schema(description = "변경할 게임 선호 시간 정보")
	private List<@Valid ScheduleEntry> schedules = new ArrayList<>();

	public MyProfileUpdateServiceRequest toServiceRequest() {
		return MyProfileUpdateServiceRequest.builder()
			.status(status)
			.introductions(introductions)
			.preferGameModes(preferGameModes)
			.schedules(schedules)
			.build();
	}
}
