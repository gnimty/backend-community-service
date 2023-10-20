package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.IntroductionUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;

@Getter
public class IntroductionUpdateRequest {

	@Valid
	private List<IntroductionEntry> introductions;

	public IntroductionUpdateServiceRequest toServiceRequest() {
		return IntroductionUpdateServiceRequest.builder()
			.introductions(introductions)
			.build();
	}
}
