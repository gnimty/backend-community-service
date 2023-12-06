package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
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

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Status status;
	@Valid
	private List<IntroductionEntry> introductions;
	@Valid
	private List<PreferGameModeEntry> preferGameModes;
	@Valid
	private List<ScheduleEntry> schedules;

	public MyProfileUpdateServiceRequest toServiceRequest() {
		return MyProfileUpdateServiceRequest.builder()
			.status(status)
			.introductions(introductions)
			.preferGameModes(preferGameModes)
			.schedules(schedules)
			.build();
	}
}
