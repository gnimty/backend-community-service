package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordResetServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.EMAIL_PATTERN, message = ErrorMessage.INVALID_INPUT_VALUE)
    private String email;

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.PASSWORD_PATTERN, message = ErrorMessage.INVALID_INPUT_VALUE)
    private String password;

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private String uuid;

    public PasswordResetServiceRequest toServiceRequest() {
        return PasswordResetServiceRequest.builder()
            .email(email)
            .password(password)
            .uuid(uuid)
            .build();
    }
}
