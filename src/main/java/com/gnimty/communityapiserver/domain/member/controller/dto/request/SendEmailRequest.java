package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.SendEmailServiceRequest;
import com.gnimty.communityapiserver.global.constant.RequestPattern;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

    @Schema(example = "email@email.com", description = "이메일, not null, email pattern")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    @Pattern(regexp = RequestPattern.EMAIL_PATTERN, message = ErrorMessage.INVALID_INPUT_VALUE)
    private String email;

    public SendEmailServiceRequest toServiceRequest() {
        return SendEmailServiceRequest.builder()
            .email(email)
            .build();
    }
}
