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
        ABUSE - 욕설 및 혐오 발언
        OBSCENE - 음란하거나 성적인 발언
        FALSEHOOD - 허위 발언
        SPAMMING - 도배하는 댓글
        ILLEGAL_ADVERTISING - 홍보 및 불법광고
        PERSONAL_INFORMATION_EXPOSURE - 개인정보 노출
        OTHER - 기타
        """
)
@Getter
public enum ReportType {

    ABUSE,
    OBSCENE,
    FALSEHOOD,
    SPAMMING,
    ILLEGAL_ADVERTISING,
    PERSONAL_INFORMATION_EXPOSURE,
    OTHER;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static ReportType findByInput(String input) {
        return Stream.of(ReportType.values())
            .filter(c -> c.name().equals(input))
            .findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
    }
}
