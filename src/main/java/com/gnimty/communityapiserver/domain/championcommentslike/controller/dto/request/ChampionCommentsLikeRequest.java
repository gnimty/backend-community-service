package com.gnimty.communityapiserver.domain.championcommentslike.controller.dto.request;

import com.gnimty.communityapiserver.domain.championcommentslike.service.dto.request.ChampionCommentsLikeServiceRequest;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ChampionCommentsLikeRequest {

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Boolean likeOrNot;

    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Boolean cancel;

    public ChampionCommentsLikeServiceRequest toServiceRequest() {
        return ChampionCommentsLikeServiceRequest.builder()
            .likeOrNot(likeOrNot)
            .cancel(cancel)
            .build();
    }
}
