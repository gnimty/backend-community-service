package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IntroductionUpdateServiceRequest {

	private List<IntroductionEntry> introductions;
}
