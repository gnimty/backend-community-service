package com.gnimty.communityapiserver.domain.riotaccount.service.dto.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FilterPuuidServiceRequest {

	private List<String> puuids;
}
