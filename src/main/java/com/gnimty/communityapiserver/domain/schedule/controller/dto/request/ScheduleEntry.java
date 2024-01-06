package com.gnimty.communityapiserver.domain.schedule.controller.dto.request;

import static com.gnimty.communityapiserver.global.constant.Bound.MAX_HOUR;
import static com.gnimty.communityapiserver.global.constant.Bound.MIN_HOUR;

import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.global.constant.DayOfWeek;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import com.gnimty.communityapiserver.global.validation.annotation.BeforeEndTime;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@BeforeEndTime(startTime = "startTime", endTime = "endTime")
public class ScheduleEntry {

	@Schema(example = "MONDAY", description = "요일")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private DayOfWeek dayOfWeek;
	@Schema(example = "0", description = "시작 시간, not null, 최소 0, 최대 24")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	@Min(value = MIN_HOUR, message = ErrorMessage.INVALID_INPUT_VALUE)
	@Max(value = MAX_HOUR, message = ErrorMessage.INVALID_INPUT_VALUE)
	private Integer startTime;
	@Schema(example = "0", description = "종료 시간, not null, 최소 0, 최대 24")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	@Min(value = MIN_HOUR, message = ErrorMessage.INVALID_INPUT_VALUE)
	@Max(value = MAX_HOUR, message = ErrorMessage.INVALID_INPUT_VALUE)
	private Integer endTime;

	public static ScheduleEntry from(Schedule schedule) {
		return ScheduleEntry.builder()
			.dayOfWeek(schedule.getDayOfWeek())
			.startTime(schedule.getStartTime())
			.endTime(schedule.getEndTime())
			.build();
	}
}
