package com.gnimty.communityapiserver.domain.member.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OauthLoginServiceRequest {

	private String authCode;
}
