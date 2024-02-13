package com.gnimty.communityapiserver.global.config.async;

import com.gnimty.communityapiserver.global.dto.webclient.RiotAccountInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AfterRiotAccountCommitEvent {

	private RiotAccountInfo info;
	private Long id;
}
