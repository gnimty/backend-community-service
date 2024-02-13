package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.IntroductionUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
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
public class IntroductionUpdateRequest {

	@Builder.Default
	private List<@Valid IntroductionEntry> introductions = new ArrayList<>();

	public IntroductionUpdateServiceRequest toServiceRequest() {
		return IntroductionUpdateServiceRequest.builder()
			.introductions(introductions)
			.build();
	}
}
