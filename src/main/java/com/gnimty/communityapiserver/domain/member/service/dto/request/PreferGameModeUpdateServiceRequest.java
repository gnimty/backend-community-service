package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PreferGameModeUpdateServiceRequest {

	private List<PreferGameModeEntry> preferGameModes;
}
