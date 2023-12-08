package com.gnimty.communityapiserver.domain.schedule.controller.dto.request;

import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.global.constant.DayOfWeek;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import com.gnimty.communityapiserver.global.validation.annotation.BeforeEndTime;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@BeforeEndTime(startTime = "startTime", endTime = "endTime")
public class ScheduleEntry {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private DayOfWeek dayOfWeek;

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	@Min(value = 0, message = ErrorMessage.INVALID_INPUT_VALUE)
	@Max(value = 24, message = ErrorMessage.INVALID_INPUT_VALUE)
	private Integer startTime;

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	@Min(value = 0, message = ErrorMessage.INVALID_INPUT_VALUE)
	@Max(value = 24, message = ErrorMessage.INVALID_INPUT_VALUE)
	private Integer endTime;

	public static ScheduleEntry from(Schedule schedule) {
		return ScheduleEntry.builder()
			.dayOfWeek(schedule.getDayOfWeek())
			.startTime(schedule.getStartTime())
			.endTime(schedule.getEndTime())
			.build();
	}
}
