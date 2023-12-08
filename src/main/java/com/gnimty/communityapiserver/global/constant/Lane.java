package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Lane {
    TOP,
    JUNGLE,
    MIDDLE,
    BOTTOM,
    UTILITY;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Lane findByInput(String input) {
        return Stream.of(Lane.values())
            .filter(c -> c.name().equals(input))
            .findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
    }
}
