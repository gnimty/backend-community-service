package com.gnimty.communityapiserver.domain.member.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PasswordUpdateServiceRequest {

    private String currentPassword;
    private String newPassword;
}
