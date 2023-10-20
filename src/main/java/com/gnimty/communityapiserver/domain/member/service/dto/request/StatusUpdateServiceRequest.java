package com.gnimty.communityapiserver.domain.member.service.dto.request;

import com.gnimty.communityapiserver.global.constant.Status;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StatusUpdateServiceRequest {

	private Status status;
}
