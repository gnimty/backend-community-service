package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.PreferGameModeUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;

@Getter
public class PreferGameModeUpdateRequest {

	@Valid
	private List<PreferGameModeEntry> preferGameModes;

	public PreferGameModeUpdateServiceRequest toServiceRequest() {
		return PreferGameModeUpdateServiceRequest.builder()
			.preferGameModes(preferGameModes)
			.build();
	}
}
