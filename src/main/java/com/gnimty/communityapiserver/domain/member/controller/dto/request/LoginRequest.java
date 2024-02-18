package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.LoginServiceRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(example = "email@email.com", description = "이메일")
    private String email;
    @Schema(example = "Abc1234***", description = "비밀번호")
    private String password;

    public LoginServiceRequest toServiceRequest() {
        return LoginServiceRequest.builder()
            .email(email)
            .password(password)
            .build();
    }
}
