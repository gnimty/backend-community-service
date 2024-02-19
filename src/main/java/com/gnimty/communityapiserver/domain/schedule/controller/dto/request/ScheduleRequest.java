package com.gnimty.communityapiserver.domain.schedule.controller.dto.request;

import com.gnimty.communityapiserver.domain.schedule.service.dto.request.ScheduleServiceRequest;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest {

	@Builder.Default
	private List<@Valid ScheduleEntry> schedules = new ArrayList<>();

	public ScheduleServiceRequest toServiceRequest() {
		return ScheduleServiceRequest.builder()
			.schedules(schedules)
			.build();
	}
}
