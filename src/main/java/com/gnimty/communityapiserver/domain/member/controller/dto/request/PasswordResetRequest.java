package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordResetServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(example = "email@email.com", description = "이메일, not null, email pattern")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.EMAIL_PATTERN, message = ErrorMessage.INVALID_INPUT_VALUE)
    private String email;
    @Schema(example = "Abc1234***", description = "재설정할 비밀번호, not null, password pattern")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.PASSWORD_PATTERN, message = ErrorMessage.INVALID_INPUT_VALUE)
    private String password;
    @Schema(example = "uuid", description = "인증을 위한 uuid, not null")
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
