package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.oauthinfo.entity.OauthInfo;
import com.gnimty.communityapiserver.global.constant.Provider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OauthInfoEntry {

    @Schema(example = "email@kakao.com", description = "연동된 email")
    private String email;
    @Schema(example = "KAKAO", description = "서비스 제공자")
    private Provider provider;

    public static OauthInfoEntry from(OauthInfo oauthInfo) {
        return OauthInfoEntry.builder()
            .email(oauthInfo.getEmail())
            .provider(oauthInfo.getProvider())
            .build();
    }
}
