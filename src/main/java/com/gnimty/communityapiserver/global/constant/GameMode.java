package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.stream.Stream;

@Schema(
    enumAsRef = true,
    description = """
        RANK_SOLO - 솔로 랭크
        RANK_FLEX - 자유 랭크
        BLIND - 칼바람/일반 게임
        """
)
@Getter
public enum GameMode {
    RANK_SOLO,
    RANK_FLEX,
    BLIND;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GameMode findByCode(String input) {
        return Stream.of(GameMode.values())
            .filter(c -> c.name().equals(input))
            .findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
    }
}
