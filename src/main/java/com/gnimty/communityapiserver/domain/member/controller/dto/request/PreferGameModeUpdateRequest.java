package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.PreferGameModeUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
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
public class PreferGameModeUpdateRequest {

	@Builder.Default
	private List<@Valid PreferGameModeEntry> preferGameModes = new ArrayList<>();

	public PreferGameModeUpdateServiceRequest toServiceRequest() {
		return PreferGameModeUpdateServiceRequest.builder()
			.preferGameModes(preferGameModes)
			.build();
	}
}
