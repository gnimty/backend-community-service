package com.gnimty.communityapiserver.domain.member.service.dto.response;

import static com.gnimty.communityapiserver.global.constant.Bound.MAX_INTRODUCTION_CONTENT_SIZE;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class IntroductionEntry {

    @Schema(example = "1", description = "소개글 id")
    private Long id;
    @Schema(example = "content", description = "소개글 내용, not null, 최대 90자")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    @Size(max = MAX_INTRODUCTION_CONTENT_SIZE, message = ErrorMessage.INVALID_INPUT_VALUE)
    private String content;
    @Schema(example = "true", description = "대표 소개글 여부, not null")
    @NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
    private Boolean isMain;

    public static IntroductionEntry from(Introduction introduction) {
        return IntroductionEntry.builder()
            .id(introduction.getId())
            .content(introduction.getContent())
            .isMain(introduction.getIsMain())
            .build();
    }
}
