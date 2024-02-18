package com.gnimty.communityapiserver.domain.member.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordEmailVerifyServiceRequest {

    private String email;
    private String code;
}
