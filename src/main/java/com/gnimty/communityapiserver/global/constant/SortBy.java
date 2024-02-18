package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.stream.Stream;

@Schema(
    enumAsRef = true,
    description = """
        RECOMMEND - up count 순
        TIER - 티어 높은 순
        ATOZ - name 순
        """
)
public enum SortBy {

    RECOMMEND,
    TIER,
    ATOZ;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SortBy findByInput(String input) {
        return Stream.of(SortBy.values())
            .filter(c -> c.name().equals(input))
            .findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
    }
}
