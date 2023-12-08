package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

import com.gnimty.communityapiserver.domain.member.service.dto.request.EmailVerifyServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyRequest {

    @NotNull(message = INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.EMAIL_PATTERN, message = INVALID_INPUT_VALUE)
    private String email;

    @NotNull(message = INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.EMAIL_AUTH_CODE_PATTERN, message = INVALID_INPUT_VALUE)
    private String code;

    public EmailVerifyServiceRequest toServiceRequest() {
        return EmailVerifyServiceRequest.builder()
            .email(email)
            .code(code)
            .build();
    }
}
