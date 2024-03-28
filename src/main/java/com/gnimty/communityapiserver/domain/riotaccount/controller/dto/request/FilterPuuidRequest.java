package com.gnimty.communityapiserver.domain.riotaccount.controller.dto.request;

import com.gnimty.communityapiserver.domain.riotaccount.service.dto.request.FilterPuuidServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterPuuidRequest {

	@Schema(example = "123456", description = "필터링 하려는 puuid 목록")
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private List<String> puuids;

	public FilterPuuidServiceRequest toServiceRequest() {
		return FilterPuuidServiceRequest.builder()
			.puuids(puuids)
			.build();
	}
}
