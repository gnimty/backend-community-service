package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.OauthLoginServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OauthLoginRequest {

    @Schema(example = "abdclsdkalfjkasdnflkne*()-", description = "provider 인가 코드, not null")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private String authCode;
    @Schema(example = "http://localhost", description = "redirect uri, not null")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private String redirectUri;

    public OauthLoginServiceRequest toServiceRequest() {
        return OauthLoginServiceRequest.builder()
            .authCode(authCode)
            .redirectUri(redirectUri)
            .build();
    }
}
