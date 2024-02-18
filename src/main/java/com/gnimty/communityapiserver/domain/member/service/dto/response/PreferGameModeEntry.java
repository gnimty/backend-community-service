package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import com.gnimty.communityapiserver.global.constant.GameMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage.INVALID_INPUT_VALUE;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PreferGameModeEntry {

    @Schema(example = "RANK_SOLO", description = "선호 게임 모드, not null")
    @NotNull(message = INVALID_INPUT_VALUE)
    private GameMode gameMode;

    public static PreferGameModeEntry from(PreferGameMode preferGameMode) {
        return PreferGameModeEntry.builder()
            .gameMode(preferGameMode.getGameMode())
            .build();
    }
}
