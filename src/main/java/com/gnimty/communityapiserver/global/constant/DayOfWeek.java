package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import lombok.Getter;

@Schema(
    enumAsRef = true,
    description = """
        SUNDAY - 일
        MONDAY - 월
        TUESDAY - 화
        WEDNESDAY - 수
        THURSDAY - 목
        FRIDAY - 금
        SATURDAY - 토
        """
)
@Getter
public enum DayOfWeek {

    SUNDAY,
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DayOfWeek findByInput(String input) {
        return Stream.of(DayOfWeek.values())
            .filter(c -> c.name().equals(input))
            .findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
    }
}
