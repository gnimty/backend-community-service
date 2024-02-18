package com.gnimty.communityapiserver.domain.member.controller.dto.response;

import com.gnimty.communityapiserver.domain.member.service.dto.response.MyProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OauthInfoEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.RiotDependentInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyProfileResponse {

    @Schema(example = "1", description = "회원 id")
    private Long id;
    @Schema(example = "email@email.com", description = "회원 email")
    private String email;
    @Schema(example = "nickname", description = "회원 닉네임")
    private String nickname;
    @Schema(example = "1", description = "가장 좋아하는 챔피언 id")
    private Long favoriteChampionId;
    @Schema(example = "100", description = "up count")
    private Long upCount;
    @Schema(description = "riot 연동 이후 볼 수 있는 정보")
    private RiotDependentInfo riotDependentInfo;
    @Schema(description = "oauth 연동 정보")
    private List<OauthInfoEntry> oauthInfos;

    public static MyProfileResponse from(MyProfileServiceResponse response) {
        return MyProfileResponse.builder()
            .id(response.getId())
            .email(response.getEmail())
            .nickname(response.getNickname())
            .favoriteChampionId(response.getFavoriteChampionId())
            .upCount(response.getUpCount())
            .riotDependentInfo(response.getRiotDependentInfo())
            .oauthInfos(response.getOauthInfos())
            .build();
    }
}