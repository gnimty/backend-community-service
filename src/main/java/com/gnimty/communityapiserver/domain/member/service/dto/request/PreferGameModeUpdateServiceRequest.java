package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PreferGameModeUpdateServiceRequest {

    private List<PreferGameModeEntry> preferGameModes;
}
