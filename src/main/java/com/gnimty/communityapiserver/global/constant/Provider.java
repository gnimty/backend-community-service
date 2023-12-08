package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Provider {

    KAKAO,
    GOOGLE;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Provider findByInput(String input) {
        return Stream.of(Provider.values())
            .filter(c -> c.name().equals(input))
            .findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
    }
}
