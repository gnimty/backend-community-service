package com.gnimty.communityapiserver.domain.member.controller.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AuthToken {

    private String accessToken;
    private String refreshToken;
}
