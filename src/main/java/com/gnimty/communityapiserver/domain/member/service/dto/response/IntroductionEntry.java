package com.gnimty.communityapiserver.domain.member.service.dto.response;

import static com.gnimty.communityapiserver.global.constant.Bound.MAX_INTRODUCTION_CONTENT_SIZE;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class IntroductionEntry {

	private Long id;

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	@Size(max = MAX_INTRODUCTION_CONTENT_SIZE, message = ErrorMessage.INVALID_INPUT_VALUE)
	private String content;

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
