package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.oauthinfo.entity.OauthInfo;
import com.gnimty.communityapiserver.global.constant.Provider;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OauthInfoEntry {

    private String email;
    private Provider provider;

    public static OauthInfoEntry from(OauthInfo oauthInfo) {
        return OauthInfoEntry.builder()
            .email(oauthInfo.getEmail())
            .provider(oauthInfo.getProvider())
            .build();
    }
}
