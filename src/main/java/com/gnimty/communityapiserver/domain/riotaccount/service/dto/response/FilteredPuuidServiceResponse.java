package com.gnimty.communityapiserver.domain.riotaccount.service.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FilteredPuuidServiceResponse {

	List<String> puuids;
}
