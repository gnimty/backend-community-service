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
        challenger - 챌린저
        grandmaster - 그랜드 마스터
        master - 마스터
        diamond - 다이아몬드
        emerald - 에메랄드
        platinum - 플레티넘
        gold - 골드
        silver - 실버
        bronze - 브론즈
        iron - 아이언
        unknown - 언랭
        """
)
@Getter
public enum Tier {

    challenger("challenger", 9),
    grandmaster("grandmaster", 8),
    master("master", 7),
    diamond("diamond", 6),
    emerald("emerald", 5),
    platinum("platinum", 4),
    gold("gold", 3),
    silver("silver", 2),
    bronze("bronze", 1),
    iron("iron", 0),
    unknown("null", -1);

    private final String tier;
    private final Integer weight;

    Tier(String tier, Integer weight) {
        this.tier = tier;
        this.weight = weight;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Tier findByInput(String input) {
        return Stream.of(Tier.values())
            .filter(c -> c.name().equals(input))
            .findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
    }
}
