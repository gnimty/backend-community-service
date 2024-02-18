package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.SignupServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.AGREE_TERMS_MUST_BE_TRUE;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @Schema(example = "email@email.com", description = "이메일, not null, email pattern")
    @NotNull(message = INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.EMAIL_PATTERN, message = INVALID_INPUT_VALUE)
    private String email;
    @Schema(example = "Abc123****", description = "비밀번호, not null, password pattern")
    @NotNull(message = INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.PASSWORD_PATTERN, message = INVALID_INPUT_VALUE)
    private String password;
    @Schema(example = "true", description = "가입 전 약관 동의 여부, not null")
    @NotNull(message = INVALID_INPUT_VALUE)
    @AssertTrue(message = AGREE_TERMS_MUST_BE_TRUE)
    private Boolean agreeTerms;

    public SignupServiceRequest toServiceRequest() {
        return SignupServiceRequest.builder()
            .email(email)
            .password(password)
            .agreeTerms(agreeTerms)
            .build();
    }
}
