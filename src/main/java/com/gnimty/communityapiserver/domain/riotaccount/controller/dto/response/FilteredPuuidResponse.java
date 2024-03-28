package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.response.FilteredPuuidServiceResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilteredPuuidResponse {

	private List<String> puuids;

	public static FilteredPuuidResponse from(FilteredPuuidServiceResponse response) {
		return FilteredPuuidResponse.builder()
			.puuids(response.getPuuids())
			.build();
	}
}
